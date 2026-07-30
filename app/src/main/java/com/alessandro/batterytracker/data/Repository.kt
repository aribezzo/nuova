package com.alessandro.batterytracker.data

import android.content.Context
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class BatteryRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).batteryEventDao()
    private val zone: ZoneId = ZoneId.systemDefault()

    suspend fun logEvent(type: EventType, batteryPct: Int, timestamp: Long = System.currentTimeMillis()) {
        dao.insert(BatteryEvent(timestamp = timestamp, type = type, batteryPct = batteryPct))
    }

    fun observeEvents() = dao.observeAll()

    // ---------- HOME (current, ongoing session) ----------

    fun computeHomeState(events: List<BatteryEvent>, nowPct: Int, isChargingNow: Boolean): HomeState {
        if (events.isEmpty()) {
            return HomeState(nowPct, isChargingNow, System.currentTimeMillis(), 0L, 0L)
        }
        val lastUnplug = events.lastOrNull { it.type == EventType.UNPLUGGED }
        val start = lastUnplug?.timestamp ?: events.first().timestamp
        val now = System.currentTimeMillis()
        // if currently charging, the session's "active window" ended at the last PLUGGED event after start
        val lastPlugAfterStart = events.lastOrNull { it.type == EventType.PLUGGED && it.timestamp >= start }
        val effectiveEnd = if (isChargingNow && lastPlugAfterStart != null) lastPlugAfterStart.timestamp else now
        val (active, inactive) = computeScreenTime(events, start, effectiveEnd)
        return HomeState(nowPct, isChargingNow, start, active, inactive)
    }

    // ---------- SCREEN TIME HELPER ----------

    private fun computeScreenTime(events: List<BatteryEvent>, start: Long, end: Long): Pair<Long, Long> {
        if (end <= start) return 0L to 0L
        val screenEvents = events
            .filter { (it.type == EventType.SCREEN_ON || it.type == EventType.SCREEN_OFF) && it.timestamp in (start + 1) until end }
            .sortedBy { it.timestamp }

        val priorEvent = events
            .filter { (it.type == EventType.SCREEN_ON || it.type == EventType.SCREEN_OFF) && it.timestamp <= start }
            .maxByOrNull { it.timestamp }

        var isOn = priorEvent?.type == EventType.SCREEN_ON || priorEvent == null
        var cursor = start
        var active = 0L
        var inactive = 0L

        for (evt in screenEvents) {
            val delta = evt.timestamp - cursor
            if (isOn) active += delta else inactive += delta
            isOn = evt.type == EventType.SCREEN_ON
            cursor = evt.timestamp
        }
        val remaining = end - cursor
        if (isOn) active += remaining else inactive += remaining
        return active to inactive
    }

    // ---------- CHARGE SESSION HISTORY ----------

    fun computeSessionHistory(events: List<BatteryEvent>): List<ChargeSession> {
        if (events.isEmpty()) return emptyList()
        val plugEvents = events.filter { it.type == EventType.UNPLUGGED || it.type == EventType.PLUGGED }
            .sortedBy { it.timestamp }

        val sessions = mutableListOf<ChargeSession>()
        var i = 0
        while (i < plugEvents.size) {
            val evt = plugEvents[i]
            if (evt.type == EventType.UNPLUGGED) {
                val start = evt.timestamp
                val startPct = evt.batteryPct
                val next = plugEvents.drop(i + 1).firstOrNull { it.type == EventType.PLUGGED }
                val end = next?.timestamp
                val endPct = next?.batteryPct ?: (events.lastOrNull()?.batteryPct ?: startPct)
                val (active, inactive) = computeScreenTime(events, start, end ?: System.currentTimeMillis())
                sessions.add(
                    ChargeSession(
                        startTime = start,
                        endTime = end,
                        startBatteryPct = startPct,
                        endBatteryPct = endPct,
                        activeMillis = active,
                        inactiveMillis = inactive,
                        isOngoing = end == null
                    )
                )
            }
            i++
        }
        return sessions.sortedByDescending { it.startTime }
    }

    // ---------- CHARGE CYCLES COUNT ----------

    fun computeChargeCycles(events: List<BatteryEvent>): Int =
        events.count { it.type == EventType.PLUGGED }

    // ---------- DAILY BREAKDOWN ----------

    fun computeDailyStats(events: List<BatteryEvent>): List<DayStat> {
        if (events.isEmpty()) return emptyList()
        val sorted = events.sortedBy { it.timestamp }
        val plugTransitions = sorted.filter { it.type == EventType.PLUGGED || it.type == EventType.UNPLUGGED }

        val rangeStart = sorted.first().timestamp
        val rangeEnd = System.currentTimeMillis()

        // Build breakpoints: day boundaries + plug transition timestamps
        val breakpoints = sortedSetOf<Long>()
        breakpoints.add(rangeStart)
        breakpoints.add(rangeEnd)
        plugTransitions.forEach { breakpoints.add(it.timestamp) }

        var day = Instant.ofEpochMilli(rangeStart).atZone(zone).toLocalDate()
        val lastDay = Instant.ofEpochMilli(rangeEnd).atZone(zone).toLocalDate()
        while (!day.isAfter(lastDay)) {
            val midnight = day.atStartOfDay(zone).toInstant().toEpochMilli()
            if (midnight in rangeStart..rangeEnd) breakpoints.add(midnight)
            day = day.plusDays(1)
        }

        val sortedBreakpoints = breakpoints.toList().sorted()

        // determine charging state before rangeStart: last plug transition at/before rangeStart
        var currentlyCharging = sorted
            .lastOrNull { (it.type == EventType.PLUGGED || it.type == EventType.UNPLUGGED) && it.timestamp <= rangeStart }
            ?.type == EventType.PLUGGED

        val segmentsByDay = linkedMapOf<LocalDate, MutableList<DaySegment>>()

        for (idx in 0 until sortedBreakpoints.size - 1) {
            val segStart = sortedBreakpoints[idx]
            val segEnd = sortedBreakpoints[idx + 1]
            if (segEnd <= segStart) continue

            // update charging state if a transition happens exactly at segStart
            val transitionAtStart = plugTransitions.lastOrNull { it.timestamp == segStart }
            if (transitionAtStart != null) {
                currentlyCharging = transitionAtStart.type == EventType.PLUGGED
            }

            if (!currentlyCharging) {
                val startPct = batteryPctAt(sorted, segStart)
                val endPct = batteryPctAt(sorted, segEnd)
                val (active, inactive) = computeScreenTime(sorted, segStart, segEnd)
                val localDate = Instant.ofEpochMilli(segStart).atZone(zone).toLocalDate()
                segmentsByDay.getOrPut(localDate) { mutableListOf() }.add(
                    DaySegment(segStart, segEnd, active, inactive, startPct, endPct)
                )
            }
        }

        return segmentsByDay.entries
            .sortedByDescending { it.key }
            .map { (date, segs) ->
                val label = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ITALIAN)
                    .replaceFirstChar { it.uppercase() } + " " +
                    date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                DayStat(label, date.toEpochDay(), segs)
            }
    }

    private fun batteryPctAt(events: List<BatteryEvent>, timestamp: Long): Int {
        val before = events.lastOrNull { it.timestamp <= timestamp }
        val after = events.firstOrNull { it.timestamp >= timestamp }
        return before?.batteryPct ?: after?.batteryPct ?: 0
    }
}

fun formatDateTimeItalian(timestamp: Long): String {
    val dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val dayName = dt.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ITALIAN).replaceFirstChar { it.uppercase() }
    val datePart = dt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val timePart = dt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
    return "$dayName $datePart $timePart"
}

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return String.format(Locale.ITALIAN, "%02dh %02dm %02ds", h, m, s)
}

package com.alessandro.batterytracker.data

data class ChargeSession(
    val startTime: Long,
    val endTime: Long?,
    val startBatteryPct: Int,
    val endBatteryPct: Int,
    val activeMillis: Long,
    val inactiveMillis: Long,
    val isOngoing: Boolean
)

data class DaySegment(
    val start: Long,
    val end: Long,
    val activeMillis: Long,
    val inactiveMillis: Long,
    val startPct: Int,
    val endPct: Int
) {
    val drainedPct: Int get() = (startPct - endPct).coerceAtLeast(0)
}

data class DayStat(
    val dateLabel: String,
    val dateEpochDay: Long,
    val segments: List<DaySegment>
) {
    val totalActiveMillis: Long get() = segments.sumOf { it.activeMillis }
    val totalInactiveMillis: Long get() = segments.sumOf { it.inactiveMillis }
    val totalDrainedPct: Int get() = segments.sumOf { it.drainedPct }
}

data class HomeState(
    val currentPct: Int,
    val isCharging: Boolean,
    val lastUnplugTime: Long,
    val activeMillis: Long,
    val inactiveMillis: Long
)

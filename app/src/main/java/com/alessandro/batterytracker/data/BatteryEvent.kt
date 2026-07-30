package com.alessandro.batterytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EventType {
    SCREEN_ON,
    SCREEN_OFF,
    PLUGGED,
    UNPLUGGED,
    BATTERY_LEVEL
}

@Entity(tableName = "battery_events")
data class BatteryEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val type: EventType,
    val batteryPct: Int
)

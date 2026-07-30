package com.alessandro.batterytracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryEventDao {

    @Insert
    suspend fun insert(event: BatteryEvent): Long

    @Query("SELECT * FROM battery_events ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<BatteryEvent>>

    @Query("SELECT * FROM battery_events ORDER BY timestamp ASC")
    suspend fun getAll(): List<BatteryEvent>

    @Query("SELECT * FROM battery_events WHERE timestamp >= :from ORDER BY timestamp ASC")
    suspend fun getSince(from: Long): List<BatteryEvent>

    @Query("SELECT * FROM battery_events ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLast(): BatteryEvent?

    @Query("SELECT * FROM battery_events WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastOfType(type: EventType): BatteryEvent?

    @Query("SELECT COUNT(*) FROM battery_events WHERE type = 'UNPLUGGED'")
    fun observeUnpluggedCount(): Flow<Int>

    @Query("DELETE FROM battery_events")
    suspend fun clearAll()
}

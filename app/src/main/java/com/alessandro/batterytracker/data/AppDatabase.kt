package com.alessandro.batterytracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromEventType(type: EventType): String = type.name

    @TypeConverter
    fun toEventType(value: String): EventType = EventType.valueOf(value)
}

@Database(entities = [BatteryEvent::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun batteryEventDao(): BatteryEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "battery_tracker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

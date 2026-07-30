package com.alessandro.batterytracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class BatteryTrackerApp : Application() {

    companion object {
        const val CHANNEL_ID = "battery_monitor_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }
}

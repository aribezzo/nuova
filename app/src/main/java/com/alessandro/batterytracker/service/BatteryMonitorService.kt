package com.alessandro.batterytracker.service

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.alessandro.batterytracker.BatteryTrackerApp
import com.alessandro.batterytracker.MainActivity
import com.alessandro.batterytracker.R
import com.alessandro.batterytracker.data.AppDatabase
import com.alessandro.batterytracker.data.BatteryEvent
import com.alessandro.batterytracker.data.EventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BatteryMonitorService : Service() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var lastLoggedPct: Int = -1

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val pct = currentBatteryPct(intent)
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> logEvent(EventType.SCREEN_ON, pct)
                Intent.ACTION_SCREEN_OFF -> logEvent(EventType.SCREEN_OFF, pct)
                Intent.ACTION_POWER_CONNECTED -> logEvent(EventType.PLUGGED, pct)
                Intent.ACTION_POWER_DISCONNECTED -> logEvent(EventType.UNPLUGGED, pct)
                Intent.ACTION_BATTERY_CHANGED -> {
                    if (pct != lastLoggedPct) {
                        lastLoggedPct = pct
                        logEvent(EventType.BATTERY_LEVEL, pct)
                    }
                }
            }
        }
    }

    private fun currentBatteryPct(intent: Intent): Int {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return if (level >= 0 && scale > 0) (level * 100 / scale) else lastLoggedPct.coerceAtLeast(0)
    }

    private fun logEvent(type: EventType, pct: Int) {
        scope.launch {
            AppDatabase.getInstance(applicationContext).batteryEventDao()
                .insert(BatteryEvent(timestamp = System.currentTimeMillis(), type = type, batteryPct = pct))
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(receiver, filter)

        seedInitialEventIfNeeded()
    }

    private fun seedInitialEventIfNeeded() {
        scope.launch {
            val dao = AppDatabase.getInstance(applicationContext).batteryEventDao()
            if (dao.getLast() == null) {
                val stickyIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val pct = stickyIntent?.let { currentBatteryPct(it) } ?: 100
                val status = stickyIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
                lastLoggedPct = pct
                dao.insert(
                    BatteryEvent(
                        timestamp = System.currentTimeMillis(),
                        type = if (isCharging) EventType.PLUGGED else EventType.UNPLUGGED,
                        batteryPct = pct
                    )
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, openAppIntent,
            android.app.PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, BatteryTrackerApp.CHANNEL_ID)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(getString(R.string.notif_text))
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 42

        fun start(context: Context) {
            val intent = Intent(context, BatteryMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

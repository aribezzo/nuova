package com.alessandro.batterytracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alessandro.batterytracker.service.BatteryMonitorService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            BatteryMonitorService.start(context)
        }
    }
}

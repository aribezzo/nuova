package com.alessandro.batterytracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alessandro.batterytracker.service.BatteryMonitorService
import com.alessandro.batterytracker.ui.MainScreen
import com.alessandro.batterytracker.ui.theme.BatteryTrackerTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            BatteryMonitorService.start(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ensurePermissionsAndStartService()

        setContent {
            BatteryTrackerTheme {
                MainScreen()
            }
        }
    }

    private fun ensurePermissionsAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                BatteryMonitorService.start(this)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            BatteryMonitorService.start(this)
        }
    }
}

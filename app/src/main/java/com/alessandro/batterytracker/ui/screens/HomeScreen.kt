package com.alessandro.batterytracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alessandro.batterytracker.data.HomeState
import com.alessandro.batterytracker.data.formatDateTimeItalian
import com.alessandro.batterytracker.data.formatDuration
import com.alessandro.batterytracker.ui.components.BatteryWaveIndicator
import com.alessandro.batterytracker.ui.components.StatCard
import com.alessandro.batterytracker.ui.theme.CyanAccent
import com.alessandro.batterytracker.ui.theme.GreenAccent
import com.alessandro.batterytracker.ui.theme.OrangeAccent
import com.alessandro.batterytracker.ui.theme.PurpleAccent
import com.alessandro.batterytracker.ui.theme.TextSecondary

@Composable
fun HomeScreen(home: HomeState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Battery Tracker",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(28.dp))

        BatteryWaveIndicator(percentage = home.currentPct, isCharging = home.isCharging)

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (home.isCharging) "In carica..." else "In uso su batteria",
            color = if (home.isCharging) GreenAccent else CyanAccent,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(28.dp))

        StatCard(
            icon = Icons.Filled.PowerOff,
            label = "Ultima carica",
            value = formatDateTimeItalian(home.lastUnplugTime),
            accentColor = PurpleAccent,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))
        StatCard(
            icon = Icons.Filled.PhoneAndroid,
            label = "Schermo attivo (da ultima carica)",
            value = formatDuration(home.activeMillis),
            accentColor = CyanAccent,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))
        StatCard(
            icon = Icons.Filled.History,
            label = "Schermo spento (da ultima carica)",
            value = formatDuration(home.inactiveMillis),
            accentColor = TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )
        if (home.isCharging) {
            Spacer(modifier = Modifier.height(14.dp))
            StatCard(
                icon = Icons.Filled.BatteryChargingFull,
                label = "Stato",
                value = "Contatori in pausa: telefono in carica",
                accentColor = OrangeAccent,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

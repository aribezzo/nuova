package com.alessandro.batterytracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alessandro.batterytracker.data.ChargeSession
import com.alessandro.batterytracker.data.formatDateTimeItalian
import com.alessandro.batterytracker.data.formatDuration
import com.alessandro.batterytracker.ui.components.ExpandableCard
import com.alessandro.batterytracker.ui.components.StatCard
import com.alessandro.batterytracker.ui.theme.CyanAccent
import com.alessandro.batterytracker.ui.theme.GreenAccent
import com.alessandro.batterytracker.ui.theme.OrangeAccent
import com.alessandro.batterytracker.ui.theme.PurpleAccent
import com.alessandro.batterytracker.ui.theme.TextSecondary

@Composable
fun HistoryScreen(sessions: List<ChargeSession>) {
    if (sessions.isEmpty()) {
        EmptyState(text = "Nessuna sessione registrata ancora.\nStacca il caricabatterie per iniziare!")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(text = "Storico sessioni", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        }
        items(sessions) { session ->
            val durationMillis = (session.endTime ?: System.currentTimeMillis()) - session.startTime
            ExpandableCard(
                title = formatDateTimeItalian(session.startTime),
                subtitle = if (session.isOngoing) "In corso · ${formatDuration(durationMillis)}"
                else "Completata · ${formatDuration(durationMillis)}",
                accentColor = if (session.isOngoing) GreenAccent else PurpleAccent
            ) {
                Column {
                    StatCard(
                        icon = Icons.Filled.PhoneAndroid,
                        label = "Schermo attivo",
                        value = formatDuration(session.activeMillis),
                        accentColor = CyanAccent
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    StatCard(
                        icon = Icons.Filled.Timelapse,
                        label = "Schermo spento",
                        value = formatDuration(session.inactiveMillis),
                        accentColor = TextSecondary
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    StatCard(
                        icon = Icons.Filled.BatteryFull,
                        label = "Batteria consumata",
                        value = "${session.startBatteryPct}% → ${session.endBatteryPct}% (-${(session.startBatteryPct - session.endBatteryPct).coerceAtLeast(0)}%)",
                        accentColor = OrangeAccent
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(text: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

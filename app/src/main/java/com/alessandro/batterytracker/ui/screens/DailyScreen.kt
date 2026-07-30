package com.alessandro.batterytracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alessandro.batterytracker.data.DayStat
import com.alessandro.batterytracker.data.formatDuration
import com.alessandro.batterytracker.ui.components.ExpandableCard
import com.alessandro.batterytracker.ui.components.StatCard
import com.alessandro.batterytracker.ui.theme.CyanAccent
import com.alessandro.batterytracker.ui.theme.OrangeAccent
import com.alessandro.batterytracker.ui.theme.RedAccent
import com.alessandro.batterytracker.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter

@Composable
fun DailyScreen(dailyStats: List<DayStat>) {
    if (dailyStats.isEmpty()) {
        EmptyState(text = "Nessun dato giornaliero ancora disponibile.")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 80.dp // Spazio di sicurezza per la bottom bar
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Consumo giornaliero",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        items(dailyStats) { day ->
            ExpandableCard(
                title = day.dateLabel,
                subtitle = "-${day.totalDrainedPct}% batteria",
                accentColor = if (day.totalDrainedPct >= 70) RedAccent else OrangeAccent
            ) {
                Column {
                    StatCard(
                        icon = Icons.Filled.PhoneAndroid,
                        label = "Schermo attivo totale",
                        value = formatDuration(day.totalActiveMillis),
                        accentColor = CyanAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatCard(
                        icon = Icons.Filled.Timelapse,
                        label = "Schermo spento totale",
                        value = formatDuration(day.totalInactiveMillis),
                        accentColor = TextSecondary
                    )
                    if (day.segments.size > 1) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Segmenti (ricarica effettuata durante il giorno)",
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.labelSmall
                        )
                        day.segments.forEachIndexed { index, seg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            val timeFmt = DateTimeFormatter.ofPattern("HH:mm")
                            val startStr = java.time.Instant.ofEpochMilli(seg.start)
                                .atZone(java.time.ZoneId.systemDefault()).format(timeFmt)
                            val endStr = java.time.Instant.ofEpochMilli(seg.end)
                                .atZone(java.time.ZoneId.systemDefault()).format(timeFmt)
                            StatCard(
                                icon = Icons.Filled.BatteryAlert,
                                label = "Segmento ${index + 1} · $startStr - $endStr",
                                value = "Attivo ${formatDuration(seg.activeMillis)} · Spento ${formatDuration(seg.inactiveMillis)} · -${seg.drainedPct}%",
                                accentColor = OrangeAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

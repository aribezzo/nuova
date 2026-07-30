package com.alessandro.batterytracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alessandro.batterytracker.ui.theme.GreenAccent
import com.alessandro.batterytracker.ui.theme.SurfaceDarkElevated
import com.alessandro.batterytracker.ui.theme.TextSecondary

@Composable
fun CyclesScreen(cycles: Int) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cicli di ricarica",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
        Column(
            modifier = Modifier
                .size(220.dp)
                .background(SurfaceDarkElevated, CircleShape),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.BatteryChargingFull,
                contentDescription = null,
                tint = GreenAccent,
                modifier = Modifier.size(48.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            Text(text = "$cycles", fontSize = androidx.compose.ui.unit.TextUnit(56f, androidx.compose.ui.unit.TextUnitType.Sp), fontWeight = FontWeight.Bold)
            Text(text = "ricariche totali", color = TextSecondary)
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        Text(
            text = "Ogni volta che colleghi il caricabatterie, questo contatore aumenta di uno.",
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

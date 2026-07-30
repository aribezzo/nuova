package com.alessandro.batterytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alessandro.batterytracker.ui.theme.SurfaceDarkElevated
import com.alessandro.batterytracker.ui.theme.TextPrimary
import com.alessandro.batterytracker.ui.theme.TextSecondary

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDarkElevated, RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = accentColor,
            modifier = Modifier.size(28.dp)
        )
        Column {
            Text(text = label, color = TextSecondary, fontSize = 13.sp)
            Text(text = value, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

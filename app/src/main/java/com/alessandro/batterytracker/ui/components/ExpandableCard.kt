package com.alessandro.batterytracker.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alessandro.batterytracker.ui.theme.SurfaceDarkElevated
import com.alessandro.batterytracker.ui.theme.TextPrimary
import com.alessandro.batterytracker.ui.theme.TextSecondary

@Composable
fun ExpandableCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(SurfaceDarkElevated, RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = TextSecondary
            )
        }
        if (expanded) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 10.dp))
            content()
        }
    }
}

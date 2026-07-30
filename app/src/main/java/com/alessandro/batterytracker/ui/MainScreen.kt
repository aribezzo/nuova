package com.alessandro.batterytracker.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alessandro.batterytracker.ui.screens.CyclesScreen
import com.alessandro.batterytracker.ui.screens.DailyScreen
import com.alessandro.batterytracker.ui.screens.HistoryScreen
import com.alessandro.batterytracker.ui.screens.HomeScreen
import com.alessandro.batterytracker.ui.theme.BgDark
import com.alessandro.batterytracker.ui.theme.CyanAccent
import com.alessandro.batterytracker.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private data class TabItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    TabItem("Home", Icons.Filled.Home),
    TabItem("Storico", Icons.Filled.History),
    TabItem("Giornaliero", Icons.Filled.CalendarMonth),
    TabItem("Cicli", Icons.Filled.BatteryChargingFull)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = BgDark,
                contentColor = CyanAccent
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(tab.label) },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        selectedContentColor = CyanAccent,
                        unselectedContentColor = TextSecondary
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
        ) { page ->
            Column(modifier = Modifier.fillMaxSize()) {
                when (page) {
                    0 -> HomeScreen(uiState.home)
                    1 -> HistoryScreen(uiState.sessions)
                    2 -> DailyScreen(uiState.dailyStats)
                    3 -> CyclesScreen(uiState.chargeCycles)
                }
            }
        }
    }
}

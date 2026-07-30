package com.alessandro.batterytracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alessandro.batterytracker.data.BatteryEvent
import com.alessandro.batterytracker.data.BatteryRepository
import com.alessandro.batterytracker.data.ChargeSession
import com.alessandro.batterytracker.data.DayStat
import com.alessandro.batterytracker.data.EventType
import com.alessandro.batterytracker.data.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BatteryRepository(application)

    // ticks every second so ongoing counters (home screen) refresh live
    private val ticker = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                ticker.value = System.currentTimeMillis()
            }
        }
    }

    private val eventsFlow = repository.observeEvents()

    val uiState: StateFlow<UiState> = combine(eventsFlow, ticker) { events, _ ->
        buildUiState(events)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    private fun buildUiState(events: List<BatteryEvent>): UiState {
        if (events.isEmpty()) return UiState()

        val last = events.last()
        val lastPlugTransition = events.lastOrNull { it.type == EventType.PLUGGED || it.type == EventType.UNPLUGGED }
        val isCharging = lastPlugTransition?.type == EventType.PLUGGED
        val currentPct = events.lastOrNull { it.batteryPct >= 0 }?.batteryPct ?: last.batteryPct

        val home = repository.computeHomeState(events, currentPct, isCharging)
        val sessions = repository.computeSessionHistory(events)
        val daily = repository.computeDailyStats(events)
        val cycles = repository.computeChargeCycles(events)

        return UiState(
            hasData = true,
            home = home,
            sessions = sessions,
            dailyStats = daily,
            chargeCycles = cycles
        )
    }
}

data class UiState(
    val hasData: Boolean = false,
    val home: HomeState = HomeState(0, false, System.currentTimeMillis(), 0L, 0L),
    val sessions: List<ChargeSession> = emptyList(),
    val dailyStats: List<DayStat> = emptyList(),
    val chargeCycles: Int = 0
)

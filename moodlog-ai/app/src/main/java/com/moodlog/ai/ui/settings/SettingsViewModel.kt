package com.moodlog.ai.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moodlog.ai.data.preferences.SettingsRepository
import com.moodlog.ai.data.preferences.ThemeMode
import com.moodlog.ai.data.preferences.UserPreferences
import com.moodlog.ai.data.repository.InsightRepository
import com.moodlog.ai.data.repository.MoodRepository
import com.moodlog.ai.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsEvent {
    data object DataDeleted : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val settings: SettingsRepository,
    private val moodRepository: MoodRepository,
    private val insightRepository: InsightRepository
) : AndroidViewModel(application) {

    val state: StateFlow<UserPreferences> = settings.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settings.setThemeMode(mode) }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settings.setReminderEnabled(enabled)
            val current = state.value
            if (enabled) {
                ReminderScheduler.scheduleDaily(
                    context = getApplication(),
                    hour = current.reminderHour,
                    minute = current.reminderMinute,
                    replaceExisting = true
                )
            } else {
                ReminderScheduler.cancel(getApplication())
            }
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            settings.setReminderTime(hour, minute)
            if (state.value.reminderEnabled) {
                ReminderScheduler.scheduleDaily(
                    context = getApplication(),
                    hour = hour,
                    minute = minute,
                    replaceExisting = true
                )
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            moodRepository.deleteAll()
            // Cached AI insight describes deleted data, so clear it too.
            insightRepository.clearCache()
            _events.emit(SettingsEvent.DataDeleted)
        }
    }
}

package com.moodlog.ai.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moodlog.ai.data.preferences.SettingsRepository
import com.moodlog.ai.notification.NotificationChannels
import com.moodlog.ai.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val displayName: String = "",
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0,
    val isFinishing: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    application: Application,
    private val settings: SettingsRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onNameChange(name: String) = _state.update { it.copy(displayName = name) }

    fun onTimeChange(hour: Int, minute: Int) = _state.update {
        it.copy(reminderHour = hour, reminderMinute = minute)
    }

    /**
     * Persists onboarding inputs and schedules the daily reminder. When the user finishes
     * onboarding they have implicitly opted in to the default reminder, so we schedule it
     * here (with [ReminderScheduler]'s default KEEP policy in case the worker already exists).
     *
     * The completion flag is written last; AppRoot reacts to it to switch from the
     * onboarding flow to the main app.
     */
    fun finish() {
        if (_state.value.isFinishing) return
        _state.update { it.copy(isFinishing = true) }
        viewModelScope.launch {
            val s = _state.value
            settings.setDisplayName(s.displayName.trim())
            settings.setReminderTime(s.reminderHour, s.reminderMinute)
            settings.setReminderEnabled(true)

            val ctx = getApplication<Application>()
            NotificationChannels.ensureCreated(ctx)
            ReminderScheduler.scheduleDaily(
                context = ctx,
                hour = s.reminderHour,
                minute = s.reminderMinute,
                replaceExisting = true
            )

            settings.setOnboardingCompleted(true)
        }
    }

    /** Skip the setup page; we still mark onboarding completed to avoid re-showing it. */
    fun skipFinishing() {
        if (_state.value.isFinishing) return
        _state.update { it.copy(isFinishing = true) }
        viewModelScope.launch {
            settings.setOnboardingCompleted(true)
        }
    }
}

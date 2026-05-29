package com.moodlog.ai

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.moodlog.ai.data.preferences.SettingsRepository
import com.moodlog.ai.data.preferences.UserPreferences
import com.moodlog.ai.notification.NotificationChannels
import com.moodlog.ai.notification.ReminderScheduler
import com.moodlog.ai.ui.navigation.AppNavigation
import com.moodlog.ai.ui.theme.MoodLogTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result is informational; reminder scheduling is independent */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup notification channel and schedule daily reminder using stored prefs.
        // Uses lifecycleScope so the channel + schedule call happens once startup is ready;
        // ReminderScheduler.scheduleDaily uses KEEP policy by default so we won't bump the
        // next-fire time on every cold start.
        NotificationChannels.ensureCreated(this)
        lifecycleScope.launch {
            val prefs: UserPreferences = settingsRepository.preferences.first()
            if (prefs.reminderEnabled) {
                ReminderScheduler.scheduleDaily(
                    context = this@MainActivity,
                    hour = prefs.reminderHour,
                    minute = prefs.reminderMinute
                )
            } else {
                ReminderScheduler.cancel(this@MainActivity)
            }
        }

        // Ask for POST_NOTIFICATIONS permission on Android 13+ (best-effort, non-blocking).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val prefs by settingsRepository.preferences.collectAsState(initial = UserPreferences())
            MoodLogTheme(themeMode = prefs.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

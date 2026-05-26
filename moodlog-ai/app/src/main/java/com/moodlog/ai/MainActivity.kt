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
import androidx.compose.ui.Modifier
import com.moodlog.ai.notification.NotificationChannels
import com.moodlog.ai.notification.ReminderScheduler
import com.moodlog.ai.ui.navigation.AppNavigation
import com.moodlog.ai.ui.theme.MoodLogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result is informational; reminder scheduling is independent */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup notification channel and schedule daily reminder at 21:00.
        NotificationChannels.ensureCreated(this)
        ReminderScheduler.scheduleDaily(this, hour = 21, minute = 0)

        // Ask for POST_NOTIFICATIONS permission on Android 13+ (best-effort, non-blocking).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            MoodLogTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

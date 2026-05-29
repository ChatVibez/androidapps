package com.moodlog.ai.data.preferences

/**
 * Preferred app theme. Mapped to a Material3 color scheme by [com.moodlog.ai.ui.theme.MoodLogTheme].
 */
enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

/**
 * Aggregated user preferences. Persisted via [SettingsRepository] (DataStore Preferences).
 *
 * @param reminderEnabled if `true`, [com.moodlog.ai.notification.ReminderScheduler] schedules a
 *   daily notification at [reminderHour]:[reminderMinute]; otherwise the work is cancelled.
 * @param displayName optional nickname captured during onboarding; shown in greetings.
 * @param onboardingCompleted `true` after the user has finished the onboarding flow at least
 *   once. Drives the conditional start destination in [com.moodlog.ai.ui.AppRoot].
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0,
    val displayName: String = "",
    val onboardingCompleted: Boolean = false
)

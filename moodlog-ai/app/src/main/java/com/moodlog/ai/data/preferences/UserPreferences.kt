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
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 21,
    val reminderMinute: Int = 0
)

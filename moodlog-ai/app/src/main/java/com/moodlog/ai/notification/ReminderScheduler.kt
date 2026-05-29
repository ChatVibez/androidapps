package com.moodlog.ai.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME = "moodlog_daily_reminder"

    /**
     * Schedules a daily reminder notification at [hour]:[minute] (24h, local time).
     * Uses a periodic 24h worker; first run is delayed to the next occurrence of that time.
     *
     * @param replaceExisting when `true`, any existing scheduled work is replaced with the
     *   new time. Use this when the user changes the reminder time in settings. The default
     *   `false` keeps the existing schedule on app start to avoid bumping the next-fire time.
     */
    fun scheduleDaily(
        context: Context,
        hour: Int,
        minute: Int,
        replaceExisting: Boolean = false
    ) {
        val now = Calendar.getInstance()
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
        }
        val initialDelayMs = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()

        val policy =
            if (replaceExisting) ExistingPeriodicWorkPolicy.UPDATE
            else ExistingPeriodicWorkPolicy.KEEP

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, policy, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}

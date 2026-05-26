package com.moodlog.ai.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.moodlog.ai.R

object NotificationChannels {
    const val DAILY_REMINDER = "daily_reminder"

    fun ensureCreated(context: Context) {
        val nm = context.getSystemService<NotificationManager>() ?: return
        if (nm.getNotificationChannel(DAILY_REMINDER) != null) return

        val channel = NotificationChannel(
            DAILY_REMINDER,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.reminder_channel_desc)
        }
        nm.createNotificationChannel(channel)
    }
}

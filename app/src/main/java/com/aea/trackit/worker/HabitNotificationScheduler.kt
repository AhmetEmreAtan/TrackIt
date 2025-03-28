package com.aea.trackit.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.Calendar

object HabitNotificationScheduler {
    fun scheduleDailyNotifications(context: Context, hour: Int, minute: Int) {
        val currentTime = Calendar.getInstance()
        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (currentTime.after(notificationTime)) {
            notificationTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = notificationTime.timeInMillis - currentTime.timeInMillis

        val notificationRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "habit_notification_work",
            ExistingWorkPolicy.REPLACE,
            notificationRequest
        )
    }
}
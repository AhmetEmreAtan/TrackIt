package com.aea.trackit.worker

import android.content.Context
import androidx.work.*
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.util.Calendar

object HabitResetScheduler {
    fun scheduleDailyReset(context: Context) {
        val currentTime = Calendar.getInstance()
        val resetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (currentTime.after(resetTime)) {
            resetTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = resetTime.timeInMillis - currentTime.timeInMillis

        val dailyResetRequest = OneTimeWorkRequestBuilder<HabitResetWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_habit_reset",
            ExistingWorkPolicy.REPLACE,
            dailyResetRequest
        )
    }
}
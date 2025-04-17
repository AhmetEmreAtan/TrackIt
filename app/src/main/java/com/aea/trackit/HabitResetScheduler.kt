package com.aea.trackit

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object HabitResetScheduler {

    fun schedule(context: Context) {
        val currentTime = Calendar.getInstance()


        val targetTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(currentTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val delay = targetTime.timeInMillis - System.currentTimeMillis()

        val workRequest = PeriodicWorkRequestBuilder<HabitResetWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "HabitResetWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}

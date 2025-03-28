package com.aea.trackit.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aea.trackit.database.HabitDatabase
import com.aea.trackit.notifications.NotificationHelper

class HabitReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = HabitDatabase.getDatabase(applicationContext).habitDao()
        val habits = dao.getAllHabits()

        val notificationHelper = NotificationHelper(applicationContext)
        for (habit in habits) {
            if (!habit.isCompleted) {
                notificationHelper.sendNotification(habit.name)
            }
        }

        return Result.success()
    }
}
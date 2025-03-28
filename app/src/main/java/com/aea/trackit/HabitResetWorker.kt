package com.aea.trackit.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aea.trackit.database.HabitDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitResetWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val habitDao = HabitDatabase.getDatabase(applicationContext).habitDao()
                habitDao.resetDailyHabits()
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}

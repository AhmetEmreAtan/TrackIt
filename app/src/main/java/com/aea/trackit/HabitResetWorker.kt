package com.aea.trackit

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aea.trackit.data.HabitDatabase
import com.aea.trackit.data.HabitHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class HabitResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val db = HabitDatabase.getDatabase(context)
        val habitDao = db.habitDao()
        val historyDao = db.habitHistoryDao()


        val habits = habitDao.getAllHabits()


        val currentDate = System.currentTimeMillis()


        habits.forEach { habit ->
            val history = HabitHistory(
                habitId = habit.id,
                date = currentDate,
                isCompleted = habit.isCompleted
            )
            historyDao.insert(history)


            val resetHabit = habit.copy(isCompleted = false)
            habitDao.update(resetHabit)
        }

        Result.success()
    }
}

package com.aea.trackit.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao, private val habitHistoryDao: HabitHistoryDao) {

    suspend fun insert(habit: Habit) = habitDao.insert(habit)

    suspend fun update(habit: Habit) = habitDao.update(habit)

    suspend fun delete(habit: Habit) = habitDao.delete(habit)

    suspend fun getAllHabits(): List<Habit> = habitDao.getAllHabits()

    suspend fun getHabitByName(name: String): Habit? = habitDao.getHabitByName(name)

    suspend fun resetDailyHabits() = habitDao.resetDailyHabits()

    suspend fun getHistoriesBetween(start: Long, end: Long): List<HabitHistory> =
        habitHistoryDao.getHistoriesBetween(start, end)

    suspend fun insertHabitHistory(habitId: Int, timestamp: Long, isCompleted: Boolean) {
        val history = HabitHistory(
            habitId = habitId,
            date = timestamp,
            isCompleted = true
        )
        habitHistoryDao.insert(history)
    }

    suspend fun getCompletedCountForHabitBetween(
        habitId: Int,
        start: Long,
        end: Long
    ): Int = habitHistoryDao.getCompletedCountForHabitBetween(habitId, start, end)

}
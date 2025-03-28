package com.aea.trackit.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {

    suspend fun insert(habit: Habit) = habitDao.insert(habit)

    suspend fun update(habit: Habit) = habitDao.update(habit)

    suspend fun delete(habit: Habit) = habitDao.delete(habit)

    suspend fun getAllHabits(): List<Habit> = habitDao.getAllHabits()

    suspend fun getHabitByName(name: String): Habit? = habitDao.getHabitByName(name)

    suspend fun resetDailyHabits() = habitDao.resetDailyHabits()
}
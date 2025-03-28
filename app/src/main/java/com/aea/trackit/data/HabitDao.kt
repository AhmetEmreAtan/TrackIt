package com.aea.trackit.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    @Query("SELECT * FROM habits WHERE name = :name LIMIT 1")
    suspend fun getHabitByName(name: String): Habit?

    @Query("UPDATE habits SET isCompleted = 0")
    suspend fun resetDailyHabits()
}
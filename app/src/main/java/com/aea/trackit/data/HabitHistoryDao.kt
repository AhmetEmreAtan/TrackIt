package com.aea.trackit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HabitHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HabitHistory)


    @Query("SELECT * FROM habit_history WHERE date BETWEEN :start AND :end AND isCompleted = 1")
    suspend fun getHistoriesBetween(start: Long, end: Long): List<HabitHistory>


    @Query("SELECT COUNT(*) FROM habit_history WHERE date BETWEEN :startOfDay AND :endOfDay AND isCompleted = 1")
    suspend fun getCompletedHabitsToday(startOfDay: Long, endOfDay: Long): Int


    @Query("SELECT COUNT(*) FROM habit_history WHERE date BETWEEN :startOfWeek AND :endOfWeek AND isCompleted = 1")
    suspend fun getCompletedHabitsThisWeek(startOfWeek: Long, endOfWeek: Long): Int


    @Query("SELECT COUNT(*) FROM habit_history WHERE date BETWEEN :startOfMonth AND :endOfMonth AND isCompleted = 1")
    suspend fun getCompletedHabitsThisMonth(startOfMonth: Long, endOfMonth: Long): Int

    @Query("""SELECT COUNT(*) FROM habit_history WHERE habitId = :habitId AND isCompleted = 1 AND date BETWEEN :start AND :end """)
    suspend fun getCompletedCountForHabitBetween(
        habitId: Int,
        start: Long,
        end: Long
    ): Int
}
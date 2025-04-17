package com.aea.trackit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_history")
data class HabitHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: Long,
    val isCompleted: Boolean
)
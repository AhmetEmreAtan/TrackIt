package com.aea.trackit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
package com.aea.trackit.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aea.trackit.data.Habit
import com.aea.trackit.data.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _dailyCompletion = MutableStateFlow(0f)
    val dailyCompletion: StateFlow<Float> = _dailyCompletion

    private val _weeklyCompletion = MutableStateFlow(0f)
    val weeklyCompletion: StateFlow<Float> = _weeklyCompletion

    private val _monthlyCompletion = MutableStateFlow(0f)
    val monthlyCompletion: StateFlow<Float> = _monthlyCompletion

    fun addHabit(name: String, description: String?, reminderInterval: Int, reminderPerDay: Int) {
        viewModelScope.launch {
            val habit = Habit(
                name = name,
                description = description,
                reminderInterval = reminderInterval,
                reminderPerDay = reminderPerDay)
            repository.insert(habit)
            loadHabits()
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.delete(habit)
            loadHabits()
        }
    }

    fun loadHabits() {
        viewModelScope.launch {
            val habits = repository.getAllHabits()
            _habits.value = habits

            val total = habits.size.takeIf { it > 0 } ?: 1
            val completed = habits.count { it.isCompleted }

            val dailyPercentage = completed.toFloat() / total
            val weeklyPercentage = completed.toFloat() / total
            val monthlyPercentage = completed.toFloat() / total

            _dailyCompletion.value = dailyPercentage
            _weeklyCompletion.value = weeklyPercentage
            _monthlyCompletion.value = monthlyPercentage
        }
    }


    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            val updatedHabit = habit.copy(isCompleted = !habit.isCompleted)
            repository.update(updatedHabit)
            loadHabits()
        }
    }

}
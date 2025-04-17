package com.aea.trackit.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aea.trackit.data.Habit
import com.aea.trackit.data.HabitHistory
import com.aea.trackit.data.HabitRepository
import com.aea.trackit.data.HabitStat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    // Yüzdelikler
    private val _dailyCompletion = MutableStateFlow(0f)
    val dailyCompletion: StateFlow<Float> = _dailyCompletion

    private val _weeklyCompletion = MutableStateFlow(0f)
    val weeklyCompletion: StateFlow<Float> = _weeklyCompletion

    private val _monthlyCompletion = MutableStateFlow(0f)
    val monthlyCompletion: StateFlow<Float> = _monthlyCompletion

    // Sayısal veriler
    private val _dailyCompletedCount = MutableStateFlow(0)
    val dailyCompletedCount: StateFlow<Int> = _dailyCompletedCount

    private val _dailyTotalCount = MutableStateFlow(0)
    val dailyTotalCount: StateFlow<Int> = _dailyTotalCount

    private val _weeklyCompletedCount = MutableStateFlow(0)
    val weeklyCompletedCount: StateFlow<Int> = _weeklyCompletedCount

    private val _weeklyTotalCount = MutableStateFlow(0)
    val weeklyTotalCount: StateFlow<Int> = _weeklyTotalCount

    private val _monthlyCompletedCount = MutableStateFlow(0)
    val monthlyCompletedCount: StateFlow<Int> = _monthlyCompletedCount

    private val _monthlyTotalCount = MutableStateFlow(0)
    val monthlyTotalCount: StateFlow<Int> = _monthlyTotalCount

    init {
        loadHabits()
        loadStatistics()
    }

    fun addHabit(name: String, description: String?, reminderInterval: Int, reminderPerDay: Int) {
        viewModelScope.launch {
            val habit = Habit(
                name = name,
                description = description,
                reminderInterval = reminderInterval,
                reminderPerDay = reminderPerDay
            )
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
        }
    }

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            val updatedHabit = habit.copy(isCompleted = !habit.isCompleted)
            repository.update(updatedHabit)
            loadHabits()
            loadStatistics()
        }
    }

    fun loadStatistics() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance()

            // Günlük istatistik doğrudan Habit tablosundan
            val allHabits = repository.getAllHabits()
            val dailyCompleted = allHabits.count { it.isCompleted }
            val dailyTotal = allHabits.size.takeIf { it != 0 } ?: 1

            _dailyCompletedCount.value = dailyCompleted
            _dailyTotalCount.value = dailyTotal
            _dailyCompletion.value = dailyCompleted.toFloat() / dailyTotal

            // Haftalık ve Aylık istatistikler geçmişten alınır
            // Bu hafta
            calendar.timeInMillis = now
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfWeek = calendar.timeInMillis
            val endOfWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000 - 1

            // Bu ay
            calendar.timeInMillis = now
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfMonth = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val endOfMonth = calendar.timeInMillis - 1

            val weeklyHistories = repository.getHistoriesBetween(startOfWeek, endOfWeek)
            val monthlyHistories = repository.getHistoriesBetween(startOfMonth, endOfMonth)

            val weeklyCompleted = weeklyHistories.count { it.isCompleted }
            val weeklyTotal = weeklyHistories.size.takeIf { it != 0 } ?: 1
            val monthlyCompleted = monthlyHistories.count { it.isCompleted }
            val monthlyTotal = monthlyHistories.size.takeIf { it != 0 } ?: 1

            _weeklyCompletedCount.value = weeklyCompleted
            _weeklyTotalCount.value = weeklyTotal
            _weeklyCompletion.value = weeklyCompleted.toFloat() / weeklyTotal

            _monthlyCompletedCount.value = monthlyCompleted
            _monthlyTotalCount.value = monthlyTotal
            _monthlyCompletion.value = monthlyCompleted.toFloat() / monthlyTotal
        }
    }


    private fun calculateCompletion(histories: List<HabitHistory>): Float {
        if (histories.isEmpty()) return 0f
        val completed = histories.count { it.isCompleted }
        return completed.toFloat() / histories.size
    }

    fun getStatsForPeriod(
        start: Long,
        end: Long,
        days: Int,
        onResult: (List<HabitStat>) -> Unit
    ) {
        viewModelScope.launch {
            val habits = repository.getAllHabits()
            val stats = habits.map { h ->
                val c = repository.getCompletedCountForHabitBetween(h.id, start, end)
                HabitStat(h, c, days)
            }
            onResult(stats)
        }
    }

}
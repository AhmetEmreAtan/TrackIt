package com.aea.trackit.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.aea.trackit.database.HabitDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "ACTION_MARK_COMPLETE") {
            val habitName = intent.getStringExtra("habitName")

            habitName?.let { name ->
                CoroutineScope(Dispatchers.IO).launch {
                    val habitDao = HabitDatabase.getDatabase(context).habitDao()

                    val habit = habitDao.getHabitByName(name)
                    habit?.let {
                        val updatedHabit = it.copy(isCompleted = true)
                        habitDao.update(updatedHabit)
                    }
                }
            }
        }
    }
}
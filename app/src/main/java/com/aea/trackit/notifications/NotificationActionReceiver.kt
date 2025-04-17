package com.aea.trackit.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.aea.trackit.data.HabitDatabase
import com.aea.trackit.data.HabitHistoryDao
import com.aea.trackit.data.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "COMPLETE_HABIT") {
            val habitName = intent.getStringExtra("habitName") ?: return
            val notificationId = intent.getIntExtra("notificationId", -1)

            val db = HabitDatabase.getDatabase(context)
            val repository = HabitRepository(
                habitDao = db.habitDao(),
                habitHistoryDao = db.habitHistoryDao()
            )

            CoroutineScope(Dispatchers.IO).launch {
                val habit = repository.getHabitByName(habitName)
                if (habit != null) {
                    val updatedHabit = habit.copy(isCompleted = true)
                    repository.update(updatedHabit)
                    repository.insertHabitHistory(habit.id, System.currentTimeMillis(), true)

                    if (notificationId != -1) {
                        NotificationManagerCompat.from(context).cancel(notificationId)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Alışkanlık tamamlandı: $habitName", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
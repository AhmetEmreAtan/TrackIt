package com.aea.trackit.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.aea.trackit.database.HabitDatabase
import com.aea.trackit.data.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "COMPLETE_HABIT") {
            val habitName = intent.getStringExtra("habitName") ?: return
            val notificationId = intent.getIntExtra("notificationId", -1)

            val habitDao = HabitDatabase.getDatabase(context).habitDao()
            val repository = HabitRepository(habitDao)

            CoroutineScope(Dispatchers.IO).launch {
                val habit = repository.getHabitByName(habitName)
                if (habit != null) {
                    val updatedHabit = habit.copy(isCompleted = true)
                    repository.update(updatedHabit)

                    // Bildirimi iptal et
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
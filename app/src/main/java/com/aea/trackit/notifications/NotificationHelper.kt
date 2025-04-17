package com.aea.trackit.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aea.trackit.MainActivity
import com.aea.trackit.R
import com.aea.trackit.data.HabitDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationHelper(private val context: Context) {

    private val channelId = "habit_reminder_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Habit Reminder"
            val descriptionText = "Günlük alışkanlıklarını hatırlatır"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(habitName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val habitDao = HabitDatabase.getDatabase(context).habitDao()
            val habit = habitDao.getHabitByName(habitName)

            if (habit != null && !habit.isCompleted) {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_IMMUTABLE
                )

                val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = "ACTION_MARK_COMPLETE"
                    putExtra("habitName", habitName)
                }
                val completePendingIntent = PendingIntent.getBroadcast(
                    context, habitName.hashCode(), completeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.trackiticon)
                    .setContentTitle("Alışkanlık Hatırlatıcı 📅")
                    .setContentText("$habitName alışkanlığını yapmayı unutma! 🔔")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .addAction(
                        R.drawable.baseline_check_circle_outline_24,
                        "Tamamlandı",
                        completePendingIntent
                    )
                    .setColor(context.getColor(R.color.teal_700))
                    .setVibrate(longArrayOf(0, 500, 500, 500))
                    .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                    .build()

                with(NotificationManagerCompat.from(context)) {
                    notify(habitName.hashCode(), notification)
                }
            }
        }
    }
}
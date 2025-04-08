package com.aea.trackit.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aea.trackit.MainActivity
import com.aea.trackit.R
import com.aea.trackit.notifications.NotificationActionReceiver

class HabitWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val habitName = inputData.getString("habitName") ?: "Alışkanlık"

        showNotification(habitName)

        return Result.success()
    }

    private fun showNotification(habitName: String) {
        val notificationId = habitName.hashCode()

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "COMPLETE_HABIT"
            putExtra("habitName", habitName)
            putExtra("notificationId", notificationId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            completeIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "habit_channel")
            .setSmallIcon(R.drawable.trackiticon)
            .setContentTitle("TrackIt Hatırlatma")
            .setContentText("Hadi '$habitName' alışkanlığını tamamla!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.trackiticon,
                "Tamamlandı ✅",
                completePendingIntent
            )

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
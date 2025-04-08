package com.aea.trackit.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aea.trackit.R

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
        val builder = NotificationCompat.Builder(context, "habit_channel")
            .setSmallIcon(R.drawable.trackiticon)
            .setContentTitle("TrackIt Hatırlatma")
            .setContentText("Hadi '$habitName' alışkanlığını tamamla!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
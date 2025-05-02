package com.aea.trackit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aea.trackit.data.HabitDatabase
import com.aea.trackit.data.HabitRepository
import com.aea.trackit.navigation.BottomNavigationBar
import com.aea.trackit.screens.AddHabitScreen
import com.aea.trackit.screens.HabitListScreen
import com.aea.trackit.screens.StatisticsScreen
import com.aea.trackit.ui.theme.TrackItTheme
import com.aea.trackit.viewmodel.HabitViewModel
import com.aea.trackit.viewmodel.HabitViewModelFactory
import com.aea.trackit.HabitResetWorker
import com.aea.trackit.screens.DailyMotivationScreen
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


val LocalHabitViewModel = compositionLocalOf<HabitViewModel> {
    error("No HabitViewModel provided")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HabitResetScheduler.schedule(this)
        val workManager = WorkManager.getInstance(this)
        val dailyResetRequest = PeriodicWorkRequestBuilder<HabitResetWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "dailyHabitReset",
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyResetRequest
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        createNotificationChannel()

        setContent {
            val context = LocalContext.current
            val db = HabitDatabase.getDatabase(context)
            val repository = HabitRepository(
                db.habitDao(),
                db.habitHistoryDao())
            val viewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory(repository))

            TrackItTheme {
                CompositionLocalProvider(LocalHabitViewModel provides viewModel) {
                    val navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        viewModel.loadStatistics()
                    }

                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navController)
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "habit_list",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("habit_list") {
                                HabitListScreen(navController, viewModel)
                            }
                            composable("add_habit") {
                                AddHabitScreen(navController, viewModel)
                            }
                            composable("motivation_daily") {
                                DailyMotivationScreen()
                            }
                            composable("statistics") {
                                StatisticsScreen(navController, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun calculateInitialDelay(): Long {
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
        return Duration.between(now, nextMidnight).toMillis()
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Habit Reminder Channel"
            val descriptionText = "Kullanıcı alışkanlık hatırlatıcısı"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("habit_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

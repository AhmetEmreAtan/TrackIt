package com.aea.trackit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aea.trackit.data.HabitRepository
import com.aea.trackit.database.HabitDatabase
import com.aea.trackit.screens.AddHabitScreen
import com.aea.trackit.screens.HabitListScreen
import com.aea.trackit.ui.theme.TrackItTheme
import com.aea.trackit.viewmodel.HabitViewModel
import com.aea.trackit.viewmodel.HabitViewModelFactory


val LocalHabitViewModel = compositionLocalOf<HabitViewModel> {
    error("No HabitViewModel provided")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        createNotificationChannel()

        setContent {
            val context = LocalContext.current
            val db = HabitDatabase.getDatabase(context)
            val repository = HabitRepository(db.habitDao())
            val viewModel: HabitViewModel = viewModel(factory = HabitViewModelFactory(repository))

            TrackItTheme {
                CompositionLocalProvider(LocalHabitViewModel provides viewModel) {
                    val navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        viewModel.loadHabits()
                    }

                    NavHost(navController = navController, startDestination = "habit_list") {
                        composable("habit_list") {
                            HabitListScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("add_habit") {
                            AddHabitScreen(
                                navController = navController,
                                viewModel = viewModel,
                                reminderInterval = 1,
                                reminderPerDay = 1
                            )
                        }
                    }
                }
            }
        }
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

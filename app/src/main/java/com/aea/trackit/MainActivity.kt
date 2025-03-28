package com.aea.trackit

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
import com.aea.trackit.ui.theme.TrackItTheme
import com.aea.trackit.ui.screens.AddHabitScreen
import com.aea.trackit.ui.screens.HabitListScreen
import com.aea.trackit.viewmodel.HabitViewModel
import com.aea.trackit.viewmodel.HabitViewModelFactory

val LocalHabitViewModel = compositionLocalOf<HabitViewModel> {
    error("No HabitViewModel provided")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
                            AddHabitScreen(navController = navController, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
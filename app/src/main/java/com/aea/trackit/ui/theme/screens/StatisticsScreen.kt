package com.aea.trackit.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aea.trackit.viewmodel.HabitViewModel
import com.aea.trackit.ui.screens.DonutStatisticsChart
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow


@Composable
fun StatisticsScreen(navController: NavController, viewModel: HabitViewModel) {
    val dailyCompletion by viewModel.dailyCompletion.collectAsState()
    val weeklyCompletion by viewModel.weeklyCompletion.collectAsState()
    val monthlyCompletion by viewModel.monthlyCompletion.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "İstatistikler",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutStatisticsChart(
                percentage = dailyCompletion,
                label = "Bugün"
            )
            DonutStatisticsChart(
                percentage = weeklyCompletion,
                label = "Bu Hafta"
            )
            DonutStatisticsChart(
                percentage = monthlyCompletion,
                label = "Bu Ay"
            )
        }
    }
}
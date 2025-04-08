package com.aea.trackit.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aea.trackit.viewmodel.HabitViewModel
import com.aea.trackit.screens.DonutStatisticsChart
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight

@Composable
fun StatisticsScreen(navController: NavController, viewModel: HabitViewModel) {
    val dailyCompletion by viewModel.dailyCompletion.collectAsState()
    val weeklyCompletion by viewModel.weeklyCompletion.collectAsState()
    val monthlyCompletion by viewModel.monthlyCompletion.collectAsState()
    val habits by viewModel.habits.collectAsState()

    val totalHabits = habits.size
    val completedHabits = habits.count { it.isCompleted }

    val animatedDaily by animateFloatAsState(targetValue = dailyCompletion)
    val animatedWeekly by animateFloatAsState(targetValue = weeklyCompletion)
    val animatedMonthly by animateFloatAsState(targetValue = monthlyCompletion)

    val scrollState = rememberScrollState() // 🌟 Scroll state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(scrollState) // 🌟 Kaydırma buraya eklendi
    ) {
        Text(
            text = "İstatistikler",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutStatisticsChart(
                percentage = dailyCompletion,
                label = "Bugün",
                completedCount = 3,
                totalCount = 5
            )
            DonutStatisticsChart(
                percentage = weeklyCompletion,
                label = "Bu Hafta",
                completedCount = 10,
                totalCount = 20
            )
            DonutStatisticsChart(
                percentage = monthlyCompletion,
                label = "Bu Ay",
                completedCount = 50,
                totalCount = 100
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Günlük İlerleme", fontWeight = FontWeight.Bold, color = Color.Black)
        LinearProgressIndicator(
            progress = animatedDaily,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(vertical = 10.dp)
        )

        Text(text = "Haftalık İlerleme", fontWeight = FontWeight.Bold, color = Color.Black)
        LinearProgressIndicator(
            progress = animatedWeekly,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(vertical = 10.dp)
        )

        Text(text = "Aylık İlerleme", fontWeight = FontWeight.Bold, color = Color.Black)
        LinearProgressIndicator(
            progress = animatedMonthly,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Toplam Alışkanlık: $totalHabits",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Tamamlanan Alışkanlık: $completedHabits",
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                completedHabits == totalHabits && totalHabits != 0 -> "🎉 Harika! Tüm alışkanlıklar tamamlandı!"
                completedHabits == 0 -> "Başlamak için harika bir gün!"
                else -> "İyi gidiyorsun, böyle devam et!"
            },
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
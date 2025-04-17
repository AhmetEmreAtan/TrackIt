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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aea.trackit.data.HabitStat
import com.aea.trackit.viewmodel.HabitViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    // --- 1) StateFlow’lardan oku ---
    val dailyCompletion    by viewModel.dailyCompletion.collectAsState()
    val weeklyCompletion   by viewModel.weeklyCompletion.collectAsState()
    val monthlyCompletion  by viewModel.monthlyCompletion.collectAsState()

    val dailyCompleted     by viewModel.dailyCompletedCount.collectAsState()
    val dailyTotal         by viewModel.dailyTotalCount.collectAsState()

    val weeklyCompleted    by viewModel.weeklyCompletedCount.collectAsState()
    val weeklyTotal        by viewModel.weeklyTotalCount.collectAsState()

    val monthlyCompleted   by viewModel.monthlyCompletedCount.collectAsState()
    val monthlyTotal       by viewModel.monthlyTotalCount.collectAsState()

    val habits              by viewModel.habits.collectAsState()

    // Bir kere istatistikleri yükle
    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }

    // Animasyonlu progress değerleri
    val animatedDaily   by animateFloatAsState(targetValue = dailyCompletion)
    val animatedWeekly  by animateFloatAsState(targetValue = weeklyCompletion)
    val animatedMonthly by animateFloatAsState(targetValue = monthlyCompletion)

    // --- 2) BottomSheet state ---
    val sheetState    = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var isSheetOpen   by remember { mutableStateOf(false) }
    var selectedStat  by remember { mutableStateOf<String?>(null) }
    var statsForSheet by remember { mutableStateOf<List<HabitStat>>(emptyList()) }

    // Triple<startTs, endTs, daysCount>
    fun periodBounds(stat: String): Triple<Long, Long, Int> {
        val now    = System.currentTimeMillis()
        val oneDay = 24L * 60 * 60 * 1000
        return when (stat) {
            "Bugün" -> {
                val start = now - (now % oneDay)
                Triple(start, start + oneDay - 1, 1)
            }
            "Bu Hafta" -> {
                val cal = Calendar.getInstance().apply {
                    timeInMillis = now
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val start = cal.timeInMillis
                Triple(start, start + 7 * oneDay - 1, 7)
            }
            "Bu Ay" -> {
                val cal = Calendar.getInstance().apply {
                    timeInMillis = now
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val start = cal.timeInMillis
                cal.add(Calendar.MONTH, 1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val end  = cal.timeInMillis - 1
                val days = ((end - start) / oneDay).toInt() + 1
                Triple(start, end, days)
            }
            else -> Triple(now, now, 1)
        }
    }

    // Açılır açılmaz dönemi hesapla ve veriyi çek
    if (isSheetOpen && selectedStat != null) {
        val (start, end, days) = periodBounds(selectedStat!!)
        LaunchedEffect(selectedStat) {
            viewModel.getStatsForPeriod(start, end, days) { list ->
                statsForSheet = list
            }
        }

        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            sheetState = sheetState
        ) {
            Text(
                text = "Detaylı İstatistikler – ${selectedStat!!}",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            statsForSheet.forEach { stat ->
                Text(
                    text = "- ${stat.habit.name}: ${stat.completed}/${stat.totalDays}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 16.sp
                )
            }
        }
    }

    // --- 3) Ana UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Başlık
        Text(
            text = "İstatistikler",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Donut Chart'lar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutStatisticsChart(
                percentage     = dailyCompletion,
                label          = "Bugün",
                completedCount = dailyCompleted,
                totalCount     = dailyTotal,
                onClick        = {
                    selectedStat = "Bugün"
                    isSheetOpen  = true
                }
            )
            DonutStatisticsChart(
                percentage     = weeklyCompletion,
                label          = "Bu Hafta",
                completedCount = weeklyCompleted,
                totalCount     = weeklyTotal,
                onClick        = {
                    selectedStat = "Bu Hafta"
                    isSheetOpen  = true
                }
            )
            DonutStatisticsChart(
                percentage     = monthlyCompletion,
                label          = "Bu Ay",
                completedCount = monthlyCompleted,
                totalCount     = monthlyTotal,
                onClick        = {
                    selectedStat = "Bu Ay"
                    isSheetOpen  = true
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Linear Progress Bars
        Text(text = "Günlük İlerleme", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = animatedDaily,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(vertical = 8.dp)
        )

        Text(text = "Haftalık İlerleme", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = animatedWeekly,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(vertical = 8.dp)
        )

        Text(text = "Aylık İlerleme", fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = animatedMonthly,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sayısal özet
        Text(
            text = "Toplam Alışkanlık: ${habits.size}",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            text = "Tamamlanan Alışkanlık: ${habits.count { it.isCompleted }}",
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                habits.isNotEmpty() && habits.all { it.isCompleted } -> "🎉 Harika! Tüm alışkanlıklar tamamlandı!"
                habits.none { it.isCompleted }                      -> "Başlamak için harika bir gün!"
                else                                                -> "İyi gidiyorsun, böyle devam et!"
            },
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
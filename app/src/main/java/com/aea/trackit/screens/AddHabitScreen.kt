package com.aea.trackit.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aea.trackit.viewmodel.HabitViewModel
import com.aea.trackit.worker.HabitWorker
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    navController: NavController,
    viewModel: HabitViewModel,
    reminderInterval: Int = 1,
    reminderPerDay: Int = 1
) {
    val context = LocalContext.current

    var habitName by remember { mutableStateOf("") }
    var habitDescription by remember { mutableStateOf("") }

    var reminderInterval by remember { mutableStateOf(1) }
    var remindersPerDay by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Yeni Alışkanlık Ekle",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.DarkGray
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text("Alışkanlık Adı") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = habitDescription,
                    onValueChange = { habitDescription = it },
                    label = { Text("Açıklama (İsteğe Bağlı)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Hatırlatma Gün Aralığı: $reminderInterval gün",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Slider(
                    value = reminderInterval.toFloat(),
                    onValueChange = { reminderInterval = it.toInt().coerceAtLeast(1) },
                    valueRange = 1f..30f,
                    steps = 28,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Günlük Hatırlatma Sayısı: $remindersPerDay",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Slider(
                    value = remindersPerDay.toFloat(),
                    onValueChange = { remindersPerDay = it.toInt().coerceAtLeast(1) },
                    valueRange = 1f..24f,
                    steps = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (habitName.isNotBlank()) {
                            viewModel.addHabit(habitName, habitDescription, reminderPerDay = 1 , reminderInterval = 1)

                            val workManager = WorkManager.getInstance(context)
                            val data = Data.Builder()
                                .putString("habitName", habitName)
                                .build()

                            val intervalInMinutes = (24 * 60 * reminderInterval) / remindersPerDay
                            val interval = intervalInMinutes.coerceAtLeast(15)

                            val request = PeriodicWorkRequestBuilder<HabitWorker>(
                                interval.toLong(),
                                TimeUnit.MINUTES
                            )
                                .setInputData(data)
                                .build()

                            workManager.enqueue(request)

                            Toast.makeText(context, "Alışkanlık eklendi", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Lütfen bir ad girin", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    )
                ) {
                    Text(
                        "Ekle",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}
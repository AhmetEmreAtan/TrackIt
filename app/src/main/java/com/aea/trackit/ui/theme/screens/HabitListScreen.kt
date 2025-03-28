package com.aea.trackit.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aea.trackit.viewmodel.HabitViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.aea.trackit.data.Habit
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HabitListScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    val habits by viewModel.habits.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    if (showDialog && habitToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Alışkanlığı Sil") },
            text = { Text("Bu alışkanlığı silmek istiyor musun?") },
            confirmButton = {
                TextButton(onClick = {
                    habitToDelete?.let { viewModel.deleteHabit(it) }
                    showDialog = false
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_habit")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
        ) {
            items(habits) { habit ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .combinedClickable(
                            onClick = {
                                viewModel.toggleHabitCompletion(habit)
                            },
                            onLongClick = {
                                habitToDelete = habit
                                showDialog = true
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (habit.isCompleted) Color(0xFFD0F0C0) else Color.White
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = habit.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        habit.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Text(
                            text = if (habit.isCompleted) "Durum: Tamamlandı ✅" else "Durum: Tamamlanmadı ❌",
                            modifier = Modifier.padding(top = 8.dp),
                            color = if (habit.isCompleted) Color.Green else Color.Red
                        )
                    }
                }
            }
        }
    }
}
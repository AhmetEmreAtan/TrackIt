package com.aea.trackit.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMotivationScreen() {
    val context = LocalContext.current

    val motivationalQuotes = listOf(
        "Bugün, hayal ettiğin kişi olmak için harika bir gün!",
        "Başarı, vazgeçmeyenlerin ödülüdür.",
        "Küçük adımlar büyük değişimleri başlatır.",
        "Zor zamanlar geçicidir, güçlü insanlar kalıcıdır.",
        "Düşün, inan, başar!",
        "Her yeni gün, yeni bir başlangıçtır.",
        "Bugün yapacakların, yarınını şekillendirir.",
        "Sınırlarını zorla, gelişimi orada bulacaksın.",
        "Kendine inan, her şey mümkün.",
        "İlerlemenin sırrı başlamakta gizlidir."
    )

    val dailyTasks = listOf(
        "Bugün 10 dakika yürüyüş yap 🚶",
        "3 derin nefes al ve bırak 🧘",
        "Su içmeyi unutma 💧",
        "5 dakikalığına telefonunu bırak 📵",
        "Bugün birini mutlu edecek bir şey yap 💙"
    )

    val today = LocalDate.now()
    val quoteIndex = today.dayOfYear % motivationalQuotes.size
    val taskIndex = today.dayOfYear % dailyTasks.size

    val quoteOfTheDay = motivationalQuotes[quoteIndex]
    val taskOfTheDay = dailyTasks[taskIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.White, Color(0xFFE3F2FD))))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Motivasyon İkonu",
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Günün Sözü",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = quoteOfTheDay,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Günlük görev kartı
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Günün Görevi 💪",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = taskOfTheDay,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Harikasın! Yeni hedefler seni bekliyor 🌟", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text(text = "Motivasyonumu Aldım!", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
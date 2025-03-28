package com.aea.trackit.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationPreview(habitName: String = "Su İç") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(
                text = "Alışkanlık Hatırlatıcı 📅",
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$habitName alışkanlığını yapmayı unutma!",
                fontSize = 16.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: Tamamlandı butonuna tıklanınca ne olacak? */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tamamlandı", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotification() {
    NotificationPreview()
}
package com.aea.trackit.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun DonutStatisticsChart(
    percentage: Float,
    label: String,
    completedCount: Int,
    totalCount: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000),
        label = ""
    )

    Column(
        modifier = Modifier.width(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                val size = size.minDimension
                val strokeWidth = 24.dp.toPx()

                drawArc(
                    color = Color.LightGray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                    size = Size(size, size),
                    topLeft = Offset.Zero
                )

                val brush = Brush.sweepGradient(
                    listOf(Color(0xFF3F51B5), Color(0xFF4CAF50), Color(0xFFFF9800))
                )

                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = 360 * animatedProgress,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                    size = Size(size, size),
                    topLeft = Offset.Zero
                )
            }

            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Alt yazılar
        Text(
            text = label,
            color = Color.DarkGray,
            fontSize = 16.sp
        )

        Text(
            text = "$completedCount / $totalCount tamamlandı 🎉",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}
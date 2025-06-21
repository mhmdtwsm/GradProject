package com.example.project1.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.statistics.StatisticsManager
import com.example.project1.statistics.UserStatistics
import java.lang.reflect.Method

@Composable
fun HomeButtons(title: String, icon: Int, iconSize: Int = 70, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp,
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2431))
                .border(3.dp, Color.Gray, RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(iconSize.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HomeButtonsWithChart(
    title: String,
    icon: Int,
    iconSize: Int = 70,
    isUrlChart: Boolean = true,
    onClick: () -> Unit
) {
    // Get statistics from StatisticsManager
    val context = LocalContext.current
    val statisticsManager = StatisticsManager.getInstance(context)
    val statistics by statisticsManager.statistics.collectAsState(initial = null)

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp,
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2431))
                .border(3.dp, Color.Gray, RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Draw the pie chart
                PieChart(
                    statistics = statistics,
                    isUrlChart = isUrlChart
                )

                // Display the ratio as text
                val ratio = if (isUrlChart) {
                    if (statistics != null && statistics!!.totalUrls > 0) {
                        "${statistics!!.safeUrls}/${statistics!!.totalUrls}"
                    } else {
                        "0/0"
                    }
                } else {
                    if (statistics != null && statistics!!.totalSms > 0) {
                        "${statistics!!.safeSms}/${statistics!!.totalSms}"
                    } else {
                        "0/0"
                    }
                }

                Text(
                    text = ratio,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PieChart(
    statistics: UserStatistics?,
    isUrlChart: Boolean
) {
    val safeColor = Color(0xFF2182CC) // Dark Green
    val unsafeColor = Color(0xFF000000) // Black
    val backgroundColor = Color(0xFF808080) // Gray

    Canvas(modifier = Modifier.size(100.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = minOf(canvasWidth, canvasHeight) / 2
        val center = Offset(canvasWidth / 2, canvasHeight / 2)

        // Draw background circle
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center
        )

        // Calculate the sweep angle based on statistics
        val sweepAngle = if (isUrlChart) {
            if (statistics != null && statistics.totalUrls > 0) {
                360f * statistics.safeUrls / statistics.totalUrls
            } else {
                0f
            }
        } else {
            if (statistics != null && statistics.totalSms > 0) {
                360f * statistics.safeSms / statistics.totalSms
            } else {
                0f
            }
        }

        // Draw the pie slice for safe percentage
        if (sweepAngle > 0) {
            drawArc(
                color = safeColor,
                startAngle = 0f,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }

        // Draw the pie slice for unsafe percentage
        if (sweepAngle < 360) {
            drawArc(
                color = unsafeColor,
                startAngle = sweepAngle,
                sweepAngle = 360f - sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }

        // Draw a smaller circle in the middle to create a donut chart effect
        drawCircle(
            color = Color(0xFF1C2431), // Background color of the card
            radius = radius * 0.5f,
            center = center
        )
    }
}

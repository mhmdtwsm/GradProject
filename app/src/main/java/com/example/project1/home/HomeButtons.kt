package com.example.project1.home.temp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.project1.R
import com.example.project1.statistics.StatisticsManager
import com.example.project1.statistics.UserStatistics
import com.example.project1.ui.theme.customColors

@Composable
fun HomeButtons(title: String, icon: Int, iconSize: Int = 70, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.cardBackground // Use theme color
        ),
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurface, // Use theme color
                modifier = Modifier.size(iconSize.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge, // Use typography from theme
                color = MaterialTheme.colorScheme.onSurface, // Use theme color
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HomeButtonsWithChart(
    title: String,
    isUrlChart: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val statisticsManager = StatisticsManager.getInstance(context)
    val statistics by statisticsManager.statistics.collectAsState(initial = null)

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.cardBackground // Use theme color
        ),
        modifier = Modifier
            .size(width = 150.dp, height = 180.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall, // Use typography
                color = MaterialTheme.colorScheme.onSurface, // Use color
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    statistics = statistics,
                    isUrlChart = isUrlChart
                )

                val ratio = if (isUrlChart) {
                    if (statistics != null && statistics!!.totalUrls > 0) {
                        "${statistics!!.safeUrls}/${statistics!!.totalUrls}"
                    } else "0/0"
                } else {
                    if (statistics != null && statistics!!.totalSms > 0) {
                        "${statistics!!.safeSms}/${statistics!!.totalSms}"
                    } else "0/0"
                }

                Text(
                    text = ratio,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun PieChart(statistics: UserStatistics?, isUrlChart: Boolean) {
    // Using semantic colors from the theme for the chart
    val safeColor = MaterialTheme.customColors.success
    val unsafeColor = MaterialTheme.customColors.danger
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val donutHoleColor = MaterialTheme.customColors.cardBackground

    Canvas(modifier = Modifier.size(100.dp)) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(color = backgroundColor, radius = radius, center = center)

        val total = (if (isUrlChart) statistics?.totalUrls else statistics?.totalSms) ?: 0
        val safeCount = (if (isUrlChart) statistics?.safeUrls else statistics?.safeSms) ?: 0

        val sweepAngle = if (total > 0) 360f * safeCount / total else 0f

        if (sweepAngle > 0) {
            drawArc(
                color = safeColor,
                startAngle = -90f, // Start from the top
                sweepAngle = sweepAngle,
                useCenter = true
            )
        }

        if (sweepAngle < 360) {
            drawArc(
                color = unsafeColor,
                startAngle = -90f + sweepAngle,
                sweepAngle = 360f - sweepAngle,
                useCenter = true
            )
        }

        // Create the donut hole
        drawCircle(color = donutHoleColor, radius = radius * 0.6f, center = center)
    }
}
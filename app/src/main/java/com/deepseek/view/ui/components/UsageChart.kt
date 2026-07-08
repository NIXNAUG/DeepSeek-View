package com.deepseek.view.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepseek.view.data.model.UsageResponse
import com.deepseek.view.ui.theme.DeepSeekBlue
import com.deepseek.view.ui.theme.DeepSeekBlueLight
import com.deepseek.view.ui.theme.Teal
import com.deepseek.view.ui.theme.TealLight

@Composable
fun UsageChart(
    usage: UsageResponse?,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Token 用量",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "刷新用量",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (usage != null && usage.totalTokens > 0) {
                // Total tokens
                Text(
                    text = formatTokenCount(usage.totalTokens),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "近30天总 Token",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                // Donut chart
                TokenDonutChart(
                    promptTokens = usage.promptTokens,
                    completionTokens = usage.completionTokens,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(
                        color = DeepSeekBlue,
                        label = "输入 Tokens",
                        count = usage.promptTokens
                    )
                    LegendItem(
                        color = Teal,
                        label = "输出 Tokens",
                        count = usage.completionTokens
                    )
                }

                // Model breakdown
                if (usage.models.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "按模型分布",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    usage.models.forEach { model ->
                        ModelUsageRow(
                            modelName = model.modelName,
                            tokens = model.totalTokens,
                            totalTokens = usage.totalTokens
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            } else if (!isLoading) {
                Text(
                    text = "暂无用量数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TokenDonutChart(
    promptTokens: Long,
    completionTokens: Long,
    modifier: Modifier = Modifier
) {
    val total = promptTokens + completionTokens
    if (total == 0L) return

    val promptAngle = (promptTokens.toFloat() / total) * 360f
    val completionAngle = (completionTokens.toFloat() / total) * 360f

    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.25f
        val arcSize = Size(
            size.width - strokeWidth,
            size.height - strokeWidth
        )
        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

        // Background arc
        drawArc(
            color = Color.LightGray.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )

        // Prompt tokens
        drawArc(
            color = DeepSeekBlue,
            startAngle = -90f,
            sweepAngle = promptAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )

        // Completion tokens
        drawArc(
            color = Teal,
            startAngle = -90f + promptAngle,
            sweepAngle = completionAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    count: Long
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = color)
        }
        Spacer(Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTokenCount(count),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ModelUsageRow(
    modelName: String,
    tokens: Long,
    totalTokens: Long
) {
    val fraction = if (totalTokens > 0) tokens.toFloat() / totalTokens else 0f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = modelName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formatTokenCount(tokens),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(Modifier.height(4.dp))
        // Progress bar
        androidx.compose.material3.LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = DeepSeekBlueLight,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

fun formatTokenCount(tokens: Long): String {
    return when {
        tokens >= 1_000_000_000 -> String.format("%.1fB", tokens / 1_000_000_000.0)
        tokens >= 1_000_000 -> String.format("%.1fM", tokens / 1_000_000.0)
        tokens >= 1_000 -> String.format("%.1fK", tokens / 1_000.0)
        else -> tokens.toString()
    }
}

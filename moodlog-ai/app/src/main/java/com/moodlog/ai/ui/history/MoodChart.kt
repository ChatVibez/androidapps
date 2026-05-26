package com.moodlog.ai.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Lightweight line chart for mood scores over time.
 * Each point's x is index in [points], y is value clamped to [1, 10].
 */
@Composable
fun MoodChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    val safePoints = remember(points) { points.ifEmpty { listOf(5f, 5f) } }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val padX = 16f
        val padY = 12f
        val plotW = w - padX * 2
        val plotH = h - padY * 2
        val minY = 1f
        val maxY = 10f

        // Grid: horizontal lines at 1, 5, 10
        val dashed = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
        listOf(1f, 5f, 10f).forEach { v ->
            val y = padY + plotH * (1f - (v - minY) / (maxY - minY))
            drawLine(
                color = gridColor,
                start = Offset(padX, y),
                end = Offset(w - padX, y),
                strokeWidth = 1f,
                pathEffect = dashed
            )
        }

        if (safePoints.size < 2) return@Canvas

        val stepX = if (safePoints.size > 1) plotW / (safePoints.size - 1) else 0f

        val path = Path()
        safePoints.forEachIndexed { idx, raw ->
            val v = raw.coerceIn(minY, maxY)
            val x = padX + stepX * idx
            val y = padY + plotH * (1f - (v - minY) / (maxY - minY))
            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4f)
        )

        // Dots
        safePoints.forEachIndexed { idx, raw ->
            val v = raw.coerceIn(minY, maxY)
            val x = padX + stepX * idx
            val y = padY + plotH * (1f - (v - minY) / (maxY - minY))
            drawCircle(
                color = lineColor,
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}

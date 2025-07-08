/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.compose.widget.chart.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.chart.model.PeraLineChartTheme
import kotlin.math.abs

@Composable
fun PeraLineChart(
    modifier: Modifier = Modifier,
    data: List<Float>,
    chartTheme: PeraLineChartTheme = getDefaultTheme(),
    onDataPointSelected: (index: Int?) -> Unit = {}
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        if (data.isEmpty()) return@BoxWithConstraints
        val density = LocalDensity.current
        val points = remember(data) { createPointOffsets(density, chartTheme, data) }
        val linePath = remember(points) { createLinePath(points) }
        var selectedOffset by remember { mutableStateOf<Offset?>(null) }
        LaunchedEffect(selectedOffset) {
            val selectedIndex = selectedOffset?.let { points.findClosestIndex(it.x) }
            onDataPointSelected(selectedIndex)
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .detectChartTapGestures(points) { offset -> selectedOffset = offset }
        ) {
            drawGradient(linePath, points, chartTheme)
            drawLine(density, linePath, chartTheme)
            drawSelectedPointLine(chartTheme, points, selectedOffset)
        }
    }
}

private fun List<Offset>.findClosestIndex(x: Float): Int? {
    return this.minByOrNull { abs(it.x - x) }?.let { closest ->
        indexOf(closest)
    }
}

private fun DrawScope.drawSelectedPointLine(
    chartTheme: PeraLineChartTheme,
    points: List<Offset>,
    selectedOffset: Offset?
) {
    selectedOffset?.let { offset ->
        val closestPoint = points.minByOrNull { abs(it.x - offset.x) }
        closestPoint?.let { point ->
            drawLine(
                color = chartTheme.selectedLineColor,
                start = Offset(point.x, 0f),
                end = Offset(point.x, size.height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            drawCircle(color = chartTheme.selectedItemBgColor, radius = 6.dp.toPx(), center = point, style = Fill)
            drawCircle(color = chartTheme.selectedItemColor, radius = 4.dp.toPx(), center = point, style = Fill)
        }
    }
}

private fun BoxWithConstraintsScope.createPointOffsets(
    density: Density,
    chartTheme: PeraLineChartTheme,
    data: List<Float>
): List<Offset> {
    val strokeSize = with(density) { chartTheme.lineSize.dp.toPx() }
    val (width, height) = with(density) { Pair(maxWidth.toPx(), maxHeight.toPx() - strokeSize) }
    val pointGap = width / (data.size - 1)
    val (minValue, maxValue) = data.getMinMaxValues()
    return data.mapIndexed { index, value ->
        val x = index * pointGap
        val y = if (minValue == maxValue) {
            height / 2
        } else {
            height - ((value - minValue) / (maxValue - minValue) * height) + (strokeSize / 2)
        }
        Offset(x, y)
    }
}

private fun List<Float>.getMinMaxValues(): Pair<Float, Float> {
    if (isEmpty()) return 0f to 0f
    var min = this[0]
    var max = this[0]
    for (value in this) {
        if (value < min) min = value
        if (value > max) max = value
    }
    return min to max
}

private fun createLinePath(points: List<Offset>): Path {
    return Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            val cp1 = Offset((prev.x + curr.x) / 2, prev.y)
            val cp2 = Offset((prev.x + curr.x) / 2, curr.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, curr.x, curr.y)
        }
    }
}

private fun DrawScope.drawGradient(linePath: Path, points: List<Offset>, theme: PeraLineChartTheme) {
    val fillPath = Path().apply {
        addPath(linePath)
        lineTo(points.last().x, size.height)
        lineTo(points.first().x, size.height)
        close()
    }
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(theme.gradientColors),
    )
}

private fun DrawScope.drawLine(density: Density, linePath: Path, theme: PeraLineChartTheme) {
    val strokeSize = with(density) { theme.lineSize.dp.toPx() }
    drawPath(
        path = linePath,
        color = theme.lineColor,
        style = Stroke(width = strokeSize, cap = StrokeCap.Round)
    )
}

@Composable
private fun getDefaultTheme(): PeraLineChartTheme {
    return PeraLineChartTheme(
        lineColor = PeraTheme.colors.helper.positive,
        gradientColors = listOf(Color(0xFF28A79B).copy(alpha = .3f), Color.Transparent),
        lineSize = 2f,
        selectedItemColor = PeraTheme.colors.helper.positive,
        selectedItemBgColor = PeraTheme.colors.background.primary,
        selectedLineColor = PeraTheme.colors.text.grayLighter
    )
}

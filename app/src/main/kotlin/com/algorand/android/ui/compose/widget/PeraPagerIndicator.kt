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

package com.algorand.android.ui.compose.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.algorand.android.ui.compose.theme.PeraTheme
import kotlin.math.absoluteValue

private val INACTIVE_ITEM_WIDTH = 4.dp
private val ACTIVE_ITEM_WIDTH = 12.dp
private val ITEM_SPACING = 6.dp
private val ITEM_HEIGHT = 4.dp

@Composable
fun PeraPagerIndicator(
    modifier: Modifier,
    pagerState: PagerState,
) {
    Row(modifier = modifier.width(getContainerWidth(pagerState.pageCount))) {
        repeat(pagerState.pageCount) { index ->
            val indicatorColor by getIndicatorColor(index, pagerState)
            val indicatorWidth by getIndicatorWidth(drawIndex = index, pagerState = pagerState)
            Canvas(
                modifier = Modifier
                    .height(ITEM_HEIGHT)
                    .width(indicatorWidth)
            ) {
                drawRoundRect(color = indicatorColor, cornerRadius = CornerRadius(ITEM_HEIGHT.toPx()))
            }
            if (index != pagerState.pageCount - 1) {
                Spacer(modifier = Modifier.width(ITEM_SPACING))
            }
        }
    }
}

private fun getContainerWidth(itemCount: Int): Dp {
    val inactiveItemsWidth = (itemCount - 1) * INACTIVE_ITEM_WIDTH
    val spacingWidth = (itemCount + 1) * ITEM_SPACING
    return inactiveItemsWidth + spacingWidth + ACTIVE_ITEM_WIDTH
}

@Composable
private fun getIndicatorColor(drawIndex: Int, pagerState: PagerState): State<Color> {
    val activeColor = PeraTheme.colors.text.main
    val inactiveColor = PeraTheme.colors.layer.gray
    val nextIndex = if (pagerState.currentPageOffsetFraction > 0) -1 else 1
    val (currentColor, targetColor) = when {
        drawIndex == pagerState.currentPage -> activeColor to inactiveColor
        drawIndex + nextIndex == pagerState.currentPage -> inactiveColor to activeColor
        else -> inactiveColor to inactiveColor
    }

    val interpolatedColor = lerp(currentColor, targetColor, pagerState.currentPageOffsetFraction.absoluteValue)
    return animateColorAsState(
        targetValue = interpolatedColor,
        label = "indicator color animation",
    )
}

@Composable
private fun getIndicatorWidth(drawIndex: Int, pagerState: PagerState): State<Dp> {
    val scrollPercentage = pagerState.currentPageOffsetFraction
    val factor = scrollPercentage.absoluteValue * (ACTIVE_ITEM_WIDTH - INACTIVE_ITEM_WIDTH)
    val nextIndex = if (scrollPercentage > 0) -1 else 1
    val targetValue = when {
        drawIndex == pagerState.currentPage -> ACTIVE_ITEM_WIDTH - factor
        drawIndex + nextIndex == pagerState.currentPage -> INACTIVE_ITEM_WIDTH + factor
        else -> INACTIVE_ITEM_WIDTH
    }
    return animateDpAsState(
        targetValue = targetValue,
        label = "indicator width animation"
    )
}

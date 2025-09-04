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

package com.algorand.android.ui.compose.widget.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

private val SHIMMER_BG_COLOR = Color(0xFFF2F2F3)
private val SHIMMER_COLOR = Color(0xFFE4E4E7)
private const val SHIMMER_WIDTH = 300f
private val CORNER_RADIUS = 4.dp

@Composable
fun Modifier.shimmer(durationMillis: Int = 1500): Modifier {
    val transition = rememberInfiniteTransition(label = "")

    val size = LocalWindowInfo.current.containerSize.width
    var targetValue by remember {
        mutableFloatStateOf(size.toFloat())
    }
    var cornerRadius by remember { mutableFloatStateOf(0f) }

    val translateAnimation by transition.animateFloat(
        initialValue = -SHIMMER_WIDTH,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    return drawBehind {
        if (cornerRadius == 0f) {
            cornerRadius = CORNER_RADIUS.toPx()
        }
        drawRoundRect(color = SHIMMER_BG_COLOR, size = this.size, cornerRadius = CornerRadius(cornerRadius))
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    SHIMMER_COLOR.copy(alpha = .2f),
                    SHIMMER_COLOR.copy(alpha = .5f),
                    SHIMMER_COLOR,
                    SHIMMER_COLOR.copy(alpha = .5f),
                    SHIMMER_COLOR.copy(alpha = .2f),
                    Color.Transparent
                ),
                start = Offset(x = translateAnimation - SHIMMER_WIDTH, y = translateAnimation),
                end = Offset(x = translateAnimation + SHIMMER_WIDTH, y = translateAnimation),
            ),
            cornerRadius = CornerRadius(cornerRadius)
        )
    }
}

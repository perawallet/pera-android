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

package com.algorand.android.ui.compose.widget.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

private const val PIN_LENGTH = 6
private const val SHAKE_OFFSET_DP = 12f
private const val SHAKE_CYCLES = 3
private const val SHAKE_HALF_DURATION_MS = 50

@Composable
fun SixDigitPasswordContainer(
    enteredDigitCount: Int,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    onErrorAnimationEnd: () -> Unit = {}
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (!isError) {
            shakeOffset.snapTo(0f)
            return@LaunchedEffect
        }
        repeat(SHAKE_CYCLES) {
            shakeOffset.animateTo(
                targetValue = SHAKE_OFFSET_DP,
                animationSpec = tween(durationMillis = SHAKE_HALF_DURATION_MS)
            )
            shakeOffset.animateTo(
                targetValue = -SHAKE_OFFSET_DP,
                animationSpec = tween(durationMillis = SHAKE_HALF_DURATION_MS)
            )
        }
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = SHAKE_HALF_DURATION_MS)
        )
        onErrorAnimationEnd()
    }

    Row(
        modifier = modifier.graphicsLayer { translationX = shakeOffset.value },
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(PIN_LENGTH) { index ->
            PasswordDigit(
                isFilled = index < enteredDigitCount,
                isError = isError
            )
        }
    }
}

@Composable
private fun PasswordDigit(
    isFilled: Boolean,
    isError: Boolean
) {
    val colors = PeraTheme.colors
    val modifier = Modifier.size(24.dp)
    when {
        isError -> Spacer(
            modifier = modifier
                .background(color = colors.helper.negative, shape = CircleShape)
        )
        isFilled -> Spacer(
            modifier = modifier
                .background(color = colors.text.main, shape = CircleShape)
        )
        else -> Spacer(
            modifier = modifier
                .border(
                    width = 2.dp,
                    color = colors.password.unfilledDigitIcon,
                    shape = CircleShape
                )
        )
    }
}

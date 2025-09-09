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

package com.algorand.android.ui.compose.widget.button.slidetoconfirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Idle

@Composable
fun BoxWithConstraintsScope.SlideToConfirmButtonThumb(
    modifier: Modifier = Modifier,
    buttonState: State<ButtonState>,
    thumbSizeAsPx: Float,
    onDragChanged: (Float) -> Unit,
    onConfirmed: () -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var springAnimationStiffness by remember { mutableFloatStateOf(Spring.StiffnessHigh) }
    val dragOffsetAnimator = animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = spring(stiffness = springAnimationStiffness)
    )
    val density = LocalDensity.current
    val endOffset = remember { constraints.maxWidth - thumbSizeAsPx }
    val endThreshold = remember { endOffset * .95f }
    LaunchedEffect(dragOffsetAnimator.value) {
        onDragChanged(dragOffsetAnimator.value)
    }
    LaunchedEffect(buttonState.value) {
        if (buttonState.value == Idle) {
            dragOffset = 0f
        }
    }
    AnimatedVisibility(
        visible = buttonState.value == Idle,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { springAnimationStiffness = Spring.StiffnessHigh },
                        onDragEnd = {
                            springAnimationStiffness = Spring.StiffnessLow
                            dragOffset = if (dragOffset < endThreshold) {
                                0f
                            } else {
                                onConfirmed()
                                endOffset
                            }
                        },
                        onDragCancel = {
                            springAnimationStiffness = Spring.StiffnessLow
                            dragOffset = 0f
                        },
                        onHorizontalDrag = { change, distance ->
                            dragOffset = (dragOffset + distance).coerceIn(0f, endOffset)
                        }
                    )
                }
                .padding(start = with(density) { dragOffsetAnimator.value.toDp() })
                .background(color = PeraTheme.colors.button.primary.background, shape = CircleShape)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_right_arrow),
                contentDescription = null,
                tint = PeraTheme.colors.button.primary.text,
            )
        }
    }
}

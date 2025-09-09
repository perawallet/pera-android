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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Error
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Idle
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Loading
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Success

private val BACKGROUND_SHAPE = RoundedCornerShape(24.dp)
private val THUMB_SIZE = 52.dp

@Composable
fun SlideToConfirmButton(
    modifier: Modifier = Modifier,
    buttonState: State<ButtonState>,
    onConfirmed: () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        val density = LocalDensity.current
        val thumbSizeAsPx = remember { with(density) { THUMB_SIZE.toPx() } }
        val dragOffset = remember { mutableFloatStateOf(0f) }
        MainBackground()
        BackgroundOverlay(buttonState, dragOffset, thumbSizeAsPx)
        ButtonText(buttonState)
        SlideToConfirmButtonIcon(buttonState)
        SlideToConfirmButtonThumb(
            modifier = Modifier.size(THUMB_SIZE),
            buttonState = buttonState,
            onDragChanged = { dragOffset.floatValue = it },
            thumbSizeAsPx = thumbSizeAsPx,
            onConfirmed = onConfirmed
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.ButtonText(buttonState: State<ButtonState>) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = buttonState.value == Idle,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = stringResource(R.string.slide_to_confirm),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.button.secondary.text
        )
    }
}

@Composable
private fun MainBackground() {
    Box(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .background(color = PeraTheme.colors.layer.grayLighter, shape = BACKGROUND_SHAPE)
            .fillMaxSize()
    )
}

@Composable
private fun BackgroundOverlay(buttonState: State<ButtonState>, dragOffsetAnimator: State<Float>, thumbSizeAsPx: Float) {
    val density = LocalDensity.current
    Box(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .background(color = getBackgroundColor(buttonState).value, shape = BACKGROUND_SHAPE)
            .width(with(density) { dragOffsetAnimator.value.toDp() + thumbSizeAsPx.toDp() })
            .fillMaxHeight()
    )
}

@Composable
private fun getBackgroundColor(buttonState: State<ButtonState>): State<Color> {
    return animateColorAsState(
        targetValue = when (buttonState.value) {
            Idle, Loading -> PeraTheme.colors.button.helper.peraIcon
            Success -> PeraTheme.colors.helper.positive
            Error -> PeraTheme.colors.helper.negative
        }
    )
}

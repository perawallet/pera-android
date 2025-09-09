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
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat.SRC_ATOP
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.algorand.android.R
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Success

@Composable
fun BoxWithConstraintsScope.SlideToConfirmButtonIcon(buttonState: State<ButtonState>) {
    SuccessIcon(buttonState)
    LoadingIcon(buttonState)
    ErrorIcon(buttonState)
}

@Composable
private fun BoxWithConstraintsScope.LoadingIcon(buttonState: State<ButtonState>) {
    IconAnimatedVisibility(modifier = Modifier.size(40.dp), visible = buttonState.value == ButtonState.Loading) {
        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.Black.hashCode(), SRC_ATOP),
                keyPath = arrayOf("**")
            )
        )
        val preloaderLottieComposition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.pera_transaction_loading_animation)
        )
        val preloaderProgress by animateLottieCompositionAsState(
            preloaderLottieComposition,
            iterations = LottieConstants.IterateForever,
            isPlaying = true
        )
        LottieAnimation(
            composition = preloaderLottieComposition,
            progress = { preloaderProgress },
            dynamicProperties = dynamicProperties
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.SuccessIcon(buttonState: State<ButtonState>) {
    IconAnimatedVisibility(modifier = Modifier.size(24.dp), visible = buttonState.value == Success) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.ErrorIcon(buttonState: State<ButtonState>) {
    IconAnimatedVisibility(modifier = Modifier.size(24.dp), visible = buttonState.value == ButtonState.Error) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.IconAnimatedVisibility(
    modifier: Modifier,
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        modifier = modifier.align(Alignment.Center),
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        content = content
    )
}

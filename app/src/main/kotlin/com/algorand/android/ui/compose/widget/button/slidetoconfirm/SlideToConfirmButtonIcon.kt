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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Loading
import com.algorand.android.ui.compose.widget.button.slidetoconfirm.SlideToConfirm.ButtonState.Success

@Composable
fun BoxWithConstraintsScope.SlideToConfirmButtonIcon(buttonState: State<ButtonState>) {
    IconAnimatedVisibility(
        visible = buttonState.value == Success,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            tint = Color.White,
            contentDescription = null
        )
    }
    IconAnimatedVisibility(
        visible = buttonState.value == Loading,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_pera),
            tint = PeraTheme.colors.button.secondary.text,
            contentDescription = null
        )
    }
    IconAnimatedVisibility(
        visible = buttonState.value == ButtonState.Error,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            tint = Color.White,
            contentDescription = null
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.IconAnimatedVisibility(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        content = content
    )
}

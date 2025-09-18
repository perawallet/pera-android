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

package com.algorand.android.ui.swap.widget.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.swap.widget.viewmodel.SwapButtonViewModel
import com.algorand.wallet.swap.domain.model.SwapQuoteV2

@Composable
fun BoxScope.SwapButtonWidget(viewModel: SwapButtonViewModel, onClick: (SwapQuoteV2) -> Unit) {
    val bottomPadding by rememberBottomPadding()
    Box(
        modifier = Modifier
            .padding(bottom = bottomPadding)
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
    ) {
        when (val viewState = viewModel.state.collectAsStateWithLifecycle().value) {
            SwapButtonViewModel.ViewState.Invisible -> Unit
            is SwapButtonViewModel.ViewState.Visible -> {
                Column {
                    Box(
                        modifier = Modifier
                            .background(color = PeraTheme.colors.layer.grayLighter)
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                    val buttonState = if (viewState.isEnabled) PeraButtonState.ENABLED else PeraButtonState.DISABLED
                    PeraPrimaryButton(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        text = stringResource(R.string.swap),
                        onClick = { viewState.quote?.let(onClick) },
                        state = buttonState
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberBottomPadding(): State<Dp> {
    val density = LocalDensity.current
    val keyboardHeight = WindowInsets.ime.getBottom(density)
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(density)

    return remember(keyboardHeight, navigationBarHeight) {
        mutableStateOf(with(density) { keyboardHeight.toDp() - navigationBarHeight.toDp() }.coerceAtLeast(0.dp))
    }
}

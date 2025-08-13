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

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.swap.widget.viewmodel.SwapButtonViewModel

@Composable
fun BoxScope.SwapButtonWidget(viewModel: SwapButtonViewModel, onClick: () -> Unit) {
    when (val viewState = viewModel.state.collectAsStateWithLifecycle().value) {
        SwapButtonViewModel.ViewState.Invisible -> Unit
        is SwapButtonViewModel.ViewState.Visible -> {
            val buttonState = if (viewState.isEnabled) PeraButtonState.ENABLED else PeraButtonState.DISABLED
            PeraPrimaryButton(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                text = stringResource(R.string.swap),
                onClick = onClick,
                state = buttonState
            )
        }
    }
}

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

package com.algorand.android.ui.swap.confirmation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AccountIcon
import com.algorand.android.ui.compose.widget.button.PeraButtonState.DISABLED
import com.algorand.android.ui.compose.widget.button.PeraButtonState.ENABLED
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus.Level3
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Content
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Idle

@Composable
fun SwapConfirmationScreen(
    viewModel: SwapConfirmationViewModel,
    listener: SwapConfirmationScreenListener
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val viewState = viewModel.state.collectAsStateWithLifecycle().value
        when (viewState) {
            Idle -> Unit
            is Content -> {
                Column {
                    Toolbar(viewState.accountDisplayName, viewState.accountIconDrawable, listener::onNavBackClick)
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 80.dp)
                            .weight(1f)
                    ) {
                        AssetAmountContainer(viewState)
                        SwapConfirmationQuoteDetailContainer(viewState, listener)
                    }
                }
                PeraPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                        .align(Alignment.BottomCenter),
                    onClick = { viewModel.confirmSwapWithCheckingPriceImpact() },
                    text = stringResource(R.string.confirm_swap),
                    state = if (viewState.priceImpact.warningStatus is Level3) DISABLED else ENABLED
                )
            }
        }
    }
}

@Composable
private fun Toolbar(
    accountDisplayName: AccountDisplayName,
    accountIconDrawablePreview: AccountIconDrawablePreview,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(40.dp)
                .clickableNoRipple(onClick = onBackClick)
                .padding(8.dp),
            painter = painterResource(R.drawable.ic_left_arrow),
            tint = PeraTheme.colors.text.main,
            contentDescription = null
        )

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = stringResource(R.string.confirm_swap),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main
            )
            Row {
                AccountIcon(
                    modifier = Modifier.size(16.dp),
                    iconDrawablePreview = accountIconDrawablePreview
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = accountDisplayName.primaryDisplayName,
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.gray
                )
            }
        }
    }
}

interface SwapConfirmationScreenListener : SwapConfirmationQuoteDetailContainerListener {
    fun onNavBackClick()
}

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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel.ViewState

@Composable
fun SwapAssetWidget(
    modifier: Modifier = Modifier,
    title: String,
    amountContent: @Composable RowScope.() -> Unit,
    viewModel: SwapAssetSelectionViewModel,
    assetSelectionChipBackgroundColor: Color,
    onAssetChipClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val viewState = viewModel.state.collectAsStateWithLifecycle()
        Row {
            TitleText(text = title)
            BalanceContent(viewState.value)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            amountContent()
            Spacer(modifier = Modifier.width(12.dp))
            SwapAssetSelectionChipButton(viewState.value, assetSelectionChipBackgroundColor, onAssetChipClick)
        }
    }
}

@Composable
private fun RowScope.TitleText(text: String) {
    Text(
        modifier = Modifier.weight(1f),
        text = text,
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun BalanceContent(viewState: ViewState) {
    when (viewState) {
        is ViewState.Content -> {
            val formattedBalance = viewState.balanceRenderer.getDisplayValue()
            Text(
                text = stringResource(R.string.balance_formatted, formattedBalance, formattedBalance),
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
        ViewState.Loading -> Box { /* Shimmer */ }
        ViewState.Error, ViewState.Idle -> Unit
    }
}

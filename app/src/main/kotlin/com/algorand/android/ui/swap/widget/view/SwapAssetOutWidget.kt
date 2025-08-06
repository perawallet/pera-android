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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.ShimmerTextBox
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel.ViewState.Content.ContentState

@Composable
fun SwapAssetOutWidget(
    widgetViewModel: SwapWidgetViewModel,
    assetSelectionViewModel: SwapAssetSelectionViewModel
) {
    val viewState = widgetViewModel.state.collectAsStateWithLifecycle().value
    SwapAssetWidget(
        title = stringResource(R.string.you_receive),
        amountContent = { AssetOutAmountContent(viewState) },
        viewModel = assetSelectionViewModel,
        assetSelectionChipBackgroundColor = PeraTheme.colors.swap.assetOutButtonBackground
    )
}

@Composable
private fun RowScope.AssetOutAmountContent(viewState: ViewState) {
    Column(modifier = Modifier.weight(1f)) {
        when (viewState) {
            ViewState.Loading -> AmountLoadingContent()
            is Content -> AmountContent(viewState.amountRenderers, viewState.contentState)
        }
    }
}

@Composable
private fun AmountContent(renderers: Content.AmountRenderers, contentState: ContentState) {
    val primaryTextColor = if (contentState is ContentState.Quote) {
        PeraTheme.colors.text.main
    } else {
        PeraTheme.colors.text.grayLighter
    }
    Text(
        text = renderers.assetOutPrimaryAmount.getDisplayValue(),
        style = PeraTheme.typography.body.large.sansMedium,
        color = primaryTextColor
    )
    Text(
        text = renderers.assetOutSecondaryAmount.getDisplayValue(),
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun AmountLoadingContent() {
    ShimmerTextBox(textStyle = PeraTheme.typography.body.large.sansMedium, width = 120.dp)
    ShimmerTextBox(textStyle = PeraTheme.typography.footnote.sans, width = 80.dp)
}

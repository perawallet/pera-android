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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.swap.widget.viewmodel.SwapProviderWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapProviderWidgetViewModel.ViewState.Content
import com.algorand.android.ui.swap.widget.viewmodel.SwapProviderWidgetViewModel.ViewState.Idle

@Composable
fun SwapProviderWidget(modifier: Modifier, viewModel: SwapProviderWidgetViewModel) {
    when (val viewState = viewModel.state.collectAsStateWithLifecycle().value) {
        Idle -> Unit
        is Content -> {
            Column(modifier) {
                ProviderTitle(viewState)
                Spacer(modifier = Modifier.height(8.dp))
                ProviderDetails(viewState)
            }
        }
    }
}

@Composable
private fun ProviderTitle(content: Content) {
    val titleResId = if (content.isBestOfferSelected) R.string.provider_best_price_available else R.string.provider
    Text(
        text = stringResource(titleResId),
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun ProviderDetails(content: Content) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = content.providerName,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "1 ${content.assetInShortName} ≈ ${content.unitPrice} ${content.assetOutShortName}",
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        if (content.hasMultipleProvider) {
            Spacer(Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.ic_right_arrow),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = PeraTheme.colors.text.gray
            )
        }
    }
}

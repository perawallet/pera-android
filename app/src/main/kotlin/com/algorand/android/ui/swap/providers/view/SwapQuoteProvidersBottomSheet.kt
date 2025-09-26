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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.algorand.android.ui.swap.providers.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraRadioButton
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.PeraToolbarTextButton
import com.algorand.android.ui.compose.widget.bottomsheet.PeraModalBottomSheet
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem.Auto
import com.algorand.android.ui.swap.providers.model.SwapQuoteProviderSelectionItem.Provider
import com.algorand.android.ui.swap.providers.viewmodel.SwapQuoteProvidersViewModel
import com.algorand.android.ui.swap.providers.viewmodel.SwapQuoteProvidersViewModel.ViewState.Content
import com.algorand.android.ui.swap.providers.viewmodel.SwapQuoteProvidersViewModel.ViewState.Idle

@Composable
fun SwapQuoteProvidersBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    viewModel: SwapQuoteProvidersViewModel,
    onProviderSelected: (SwapQuoteProviderSelectionItem) -> Unit
) {
    PeraModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
        val viewState = viewModel.state.collectAsStateWithLifecycle().value
        when (viewState) {
            Idle -> Unit
            is Content -> {
                PeraToolbar(
                    text = stringResource(R.string.change_provider),
                    startContainer = {
                        Spacer(modifier = Modifier.width(12.dp))
                        PeraToolbarIcon(
                            modifier = Modifier.clickableNoRipple(onClick = onDismissRequest),
                            iconResId = R.drawable.ic_close
                        )
                    },
                    endContainer = {
                        PeraToolbarTextButton(text = stringResource(R.string.apply)) {
                            onProviderSelected(viewState.selectedItem)
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(start = 22.dp, end = 12.dp, bottom = 24.dp, top = 24.dp)
                ) {
                    items(viewState.items) { item ->
                        val isSelected = item == viewState.selectedItem
                        when (item) {
                            Auto -> AutoSelectionItem(isSelected) { viewModel.setSelectedItem(Auto) }
                            is Provider -> ProviderItem(item, isSelected) { viewModel.setSelectedItem(item) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProviderItem(item: Provider, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickableNoRipple { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SwapProviderIcon(modifier = Modifier.size(40.dp), url = item.provider.iconUrl)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.provider.displayName,
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = item.swapAmountRenderer.getDisplayValue(),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main
            )
            Text(
                text = item.selectedCurrencyValueRenderer.getDisplayValue(),
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
        PeraRadioButton(selected = isSelected, onClick = onClick)
    }
}

@Composable
private fun AutoSelectionItem(isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickableNoRipple { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sparkles_soft),
            contentDescription = null,
            modifier = Modifier
                .background(color = PeraTheme.colors.wallet.wallet4.background, shape = CircleShape)
                .size(40.dp)
                .padding(8.dp),
            tint = PeraTheme.colors.wallet.wallet4.icon
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.auto),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.best_price_available),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
        PeraRadioButton(selected = isSelected, onClick = onClick)
    }
}

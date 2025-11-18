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
@file:Suppress("LongParameterList", "LongMethod")

package com.algorand.android.ui.swap.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.widget.bottomsheet.rememberPeraSheetState
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.configuration.view.SwapConfigurationBottomSheet
import com.algorand.android.ui.swap.history.view.SwapPairHistoryWidget
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel
import com.algorand.android.ui.swap.providers.view.SwapQuoteProvidersBottomSheet
import com.algorand.android.ui.swap.providers.viewmodel.SwapQuoteProvidersViewModel
import com.algorand.android.ui.swap.topfive.view.TopSwapPairsContainer
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.widget.view.SwapButtonWidget
import com.algorand.android.ui.swap.widget.view.SwapProviderWidget
import com.algorand.android.ui.swap.widget.view.SwapWidget
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapButtonViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapConfigurationViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapProviderWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SwapScreenContentState(
    swapViewModel: SwapViewModel,
    assetInViewModel: SwapAssetSelectionViewModel,
    assetOutViewModel: SwapAssetSelectionViewModel,
    widgetViewModel: SwapWidgetViewModel,
    configViewModel: SwapConfigurationViewModel,
    providerViewModel: SwapProviderWidgetViewModel,
    quoteProvidersViewModel: SwapQuoteProvidersViewModel,
    buttonViewModel: SwapButtonViewModel,
    topSwapPairsViewModel: TopSwapPairsViewModel,
    swapPairHistoryViewModel: SwapPairHistoryViewModel,
    scope: CoroutineScope,
    listener: SwapScreenListener
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val providerBottomSheetState = rememberPeraSheetState(scope)
        val swapConfigurationBottomSheetState = rememberPeraSheetState(scope)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SwapWidget(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                swapViewModel,
                assetInViewModel,
                assetOutViewModel,
                widgetViewModel,
                providerViewModel,
                buttonViewModel,
                configViewModel,
                topSwapPairsViewModel,
                swapPairHistoryViewModel,
                listener,
                scope,
                onConfigureClick = { swapConfigurationBottomSheetState.show() },
            )
            SwapProviderWidget(
                modifier = Modifier
                    .clickableNoRipple {
                        widgetViewModel.getContentQuoteState()?.run {
                            scope.launch { swapViewModel.logSelectProviderClick() }
                            quoteProvidersViewModel.initProviders(quoteSelection, quotes.map { it.quote })
                            providerBottomSheetState.show()
                        }
                    }
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp),
                providerViewModel
            )
            Spacer(modifier = Modifier.height(24.dp))
            SwapPairHistoryWidget(
                swapPairHistoryViewModel,
                listener::onSwapHistorySeeAllClick,
                swapViewModel::setAssetInAndOutIds
            )
            Spacer(modifier = Modifier.height(24.dp))
            TopSwapPairsContainer(scope, swapViewModel, topSwapPairsViewModel)
        }
        SwapButtonWidget(buttonViewModel) { quote ->
            scope.launch { swapViewModel.logSwapButtonClick() }
            listener.onSwapClick(quote)
        }

        swapConfigurationBottomSheetState.SheetContent {
            SwapConfigurationBottomSheet(
                sheetState = swapConfigurationBottomSheetState.sheetState,
                onDismissRequest = swapConfigurationBottomSheetState::hide,
                swapViewModel = swapViewModel,
                scope = scope,
                onApplyClick = {
                    swapViewModel.applySwapConfigs(it)
                    if (it.balancePercentage != null) {
                        val percentage = it.balancePercentage
                        widgetViewModel.setAmountByPercentage(swapViewModel.getSwapDetails(), percentage)
                    }
                    swapConfigurationBottomSheetState.hide()
                }
            )
        }

        providerBottomSheetState.SheetContent {
            SwapQuoteProvidersBottomSheet(
                sheetState = providerBottomSheetState.sheetState,
                onDismissRequest = providerBottomSheetState::hide,
                viewModel = quoteProvidersViewModel,
                swapViewModel = swapViewModel,
                scope = scope,
                onProviderSelected = { selectedProvider ->
                    widgetViewModel.selectQuote(selectedProvider)
                    providerBottomSheetState.hide()
                }
            )
        }
    }
}

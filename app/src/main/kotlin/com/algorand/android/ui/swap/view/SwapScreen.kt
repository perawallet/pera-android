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
@file:Suppress("LongMethod", "LongParameterList")

package com.algorand.android.ui.swap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraSingleButtonState
import com.algorand.android.ui.compose.widget.bottomsheet.rememberPeraSheetState
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.configuration.view.SwapConfigurationBottomSheet
import com.algorand.android.ui.swap.providers.view.SwapQuoteProvidersBottomSheet
import com.algorand.android.ui.swap.providers.viewmodel.SwapQuoteProvidersViewModel
import com.algorand.android.ui.swap.topfive.view.TopSwapPairsContainer
import com.algorand.android.ui.swap.topfive.viewmodel.DefaultTopSwapPairsViewModel
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.widget.view.SwapButtonWidget
import com.algorand.android.ui.swap.widget.view.SwapProviderWidget
import com.algorand.android.ui.swap.widget.view.SwapWidget
import com.algorand.android.ui.swap.widget.view.SwapWidgetListener
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapButtonViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapConfigurationViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapProviderWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapButtonViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapConfigurationViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapProviderWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.SwapWidgetViewModel
import com.algorand.wallet.swap.domain.model.SwapQuoteV2

@Composable
fun SwapScreen(
    swapViewModel: SwapViewModel,
    assetInViewModel: DefaultSwapAssetSelectionViewModel = hiltViewModel(key = "assetInViewModel"),
    assetOutViewModel: DefaultSwapAssetSelectionViewModel = hiltViewModel(key = "assetOutViewModel"),
    widgetViewModel: SwapWidgetViewModel = hiltViewModel<DefaultSwapWidgetViewModel>(),
    configViewModel: SwapConfigurationViewModel = hiltViewModel<DefaultSwapConfigurationViewModel>(),
    providerViewModel: SwapProviderWidgetViewModel = hiltViewModel<DefaultSwapProviderWidgetViewModel>(),
    buttonViewModel: SwapButtonViewModel = hiltViewModel<DefaultSwapButtonViewModel>(),
    quoteProvidersViewModel: SwapQuoteProvidersViewModel = hiltViewModel(),
    topSwapPairsViewModel: TopSwapPairsViewModel = hiltViewModel<DefaultTopSwapPairsViewModel>(),
    listener: SwapScreenListener
) {
    LaunchedEffect(Unit) {
        swapViewModel.initViewState()
    }

    Box(
        modifier = Modifier
            .background(color = PeraTheme.colors.background.primary)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SwapToolbar(swapViewModel, listener)
            val viewState = swapViewModel.state.collectAsStateWithLifecycle()
            when (viewState.value) {
                SwapViewModel.ViewState.Idle -> Unit
                is SwapViewModel.ViewState.Content -> {
                    SwapContentState(
                        swapViewModel = swapViewModel,
                        assetInViewModel = assetInViewModel,
                        assetOutViewModel = assetOutViewModel,
                        widgetViewModel = widgetViewModel,
                        configViewModel = configViewModel,
                        providerViewModel = providerViewModel,
                        buttonViewModel = buttonViewModel,
                        quoteProvidersViewModel = quoteProvidersViewModel,
                        topSwapPairsViewModel = topSwapPairsViewModel,
                        listener = listener
                    )
                }
                SwapViewModel.ViewState.NoAccountState -> SwapNoAccountState(listener)
            }
        }
    }
}

@Composable
private fun SwapContentState(
    swapViewModel: SwapViewModel,
    assetInViewModel: SwapAssetSelectionViewModel,
    assetOutViewModel: SwapAssetSelectionViewModel,
    widgetViewModel: SwapWidgetViewModel,
    configViewModel: SwapConfigurationViewModel,
    providerViewModel: SwapProviderWidgetViewModel,
    quoteProvidersViewModel: SwapQuoteProvidersViewModel,
    buttonViewModel: SwapButtonViewModel,
    topSwapPairsViewModel: TopSwapPairsViewModel,
    listener: SwapScreenListener
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()
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
                listener,
                onConfigureClick = { swapConfigurationBottomSheetState.show() }
            )
            SwapProviderWidget(
                modifier = Modifier
                    .clickableNoRipple {
                        widgetViewModel.getContentQuoteState()?.run {
                            quoteProvidersViewModel.initProviders(quoteSelection, quotes.map { it.quote })
                            providerBottomSheetState.show()
                        }
                    }
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp),
                providerViewModel
            )
            Spacer(modifier = Modifier.height(24.dp))
            TopSwapPairsContainer(topSwapPairsViewModel)
        }
        SwapButtonWidget(buttonViewModel, listener::onSwapClick)

        swapConfigurationBottomSheetState.SheetContent {
            SwapConfigurationBottomSheet(
                sheetState = swapConfigurationBottomSheetState.sheetState,
                onDismissRequest = swapConfigurationBottomSheetState::hide,
                swapDetails = swapViewModel.getSwapDetails(),
                onApplyClick = {
                    swapViewModel.applySwapConfigs(it)
                    if (it.balancePercentage != null) {
                        val percentage = it.balancePercentage.toInt()
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
                onProviderSelected = { selectedProvider ->
                    widgetViewModel.selectQuote(selectedProvider)
                    providerBottomSheetState.hide()
                }
            )
        }
    }
}

@Composable
private fun SwapNoAccountState(listener: SwapScreenListener) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PeraSingleButtonState(
            modifier = Modifier.padding(24.dp),
            iconResId = R.drawable.ic_wallet,
            titleResId = R.string.create_an_account,
            descriptionResId = R.string.you_need_to_create,
            buttonTextResId = R.string.create_new_account,
            onClick = listener::onCreateAccountClick
        )
    }
}

interface SwapScreenListener : SwapToolbarListener, SwapWidgetListener {
    fun onCreateAccountClick()
    fun onSwapClick(quote: SwapQuoteV2)
}

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel
import com.algorand.android.ui.swap.providers.viewmodel.SwapQuoteProvidersViewModel
import com.algorand.android.ui.swap.topfive.viewmodel.DefaultTopSwapPairsViewModel
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.viewmodel.SwapViewModel.ViewState.Introduction
import com.algorand.android.ui.swap.viewmodel.SwapViewModel.ViewState.NoAccountState
import com.algorand.android.ui.swap.widget.view.SwapWidgetListener
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapAssetSelectionViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapButtonViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapConfigurationViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapProviderWidgetViewModel
import com.algorand.android.ui.swap.widget.viewmodel.DefaultSwapWidgetViewModel
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
    swapPairHistoryViewModel: SwapPairHistoryViewModel = hiltViewModel<SwapPairHistoryViewModel>(),
    listener: SwapScreenListener
) {
    Box(
        modifier = Modifier
            .background(color = PeraTheme.colors.background.primary)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val viewState = swapViewModel.state.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()
            when (viewState.value) {
                SwapViewModel.ViewState.Idle -> Unit
                is SwapViewModel.ViewState.Content -> {
                    SwapToolbar(scope, swapViewModel, listener)
                    SwapScreenContentState(
                        swapViewModel = swapViewModel,
                        assetInViewModel = assetInViewModel,
                        assetOutViewModel = assetOutViewModel,
                        widgetViewModel = widgetViewModel,
                        configViewModel = configViewModel,
                        providerViewModel = providerViewModel,
                        buttonViewModel = buttonViewModel,
                        quoteProvidersViewModel = quoteProvidersViewModel,
                        topSwapPairsViewModel = topSwapPairsViewModel,
                        swapPairHistoryViewModel = swapPairHistoryViewModel,
                        scope = scope,
                        listener = listener
                    )
                }
                NoAccountState -> {
                    SwapToolbar(scope, swapViewModel, listener)
                    SwapScreenNoAccountState(
                        scope,
                        swapViewModel,
                        topSwapPairsViewModel,
                        listener::onCreateAccountClick
                    )
                }
                Introduction -> SwapScreenIntroductionState(listener = listener)
            }
        }
    }
}

interface SwapScreenListener : SwapToolbarListener, SwapWidgetListener, SwapScreenIntroductionStateListener {
    fun onCreateAccountClick()
    fun onSwapClick(quote: SwapQuoteV2)
    fun onSwapHistorySeeAllClick()
}

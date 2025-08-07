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

package com.algorand.android.ui.swap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraSingleButtonState
import com.algorand.android.ui.swap.viewmodel.SwapViewModel
import com.algorand.android.ui.swap.widget.view.SwapButtonWidget
import com.algorand.android.ui.swap.widget.view.SwapProviderWidget
import com.algorand.android.ui.swap.widget.view.SwapWidget
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

@Composable
fun SwapScreen(
    swapViewModel: SwapViewModel = hiltViewModel(),
    assetInViewModel: DefaultSwapAssetSelectionViewModel = hiltViewModel(key = "assetInViewModel"),
    assetOutViewModel: DefaultSwapAssetSelectionViewModel = hiltViewModel(key = "assetOutViewModel"),
    widgetViewModel: SwapWidgetViewModel = hiltViewModel<DefaultSwapWidgetViewModel>(),
    configViewModel: SwapConfigurationViewModel = hiltViewModel<DefaultSwapConfigurationViewModel>(),
    providerViewModel: SwapProviderWidgetViewModel = hiltViewModel<DefaultSwapProviderWidgetViewModel>(),
    buttonViewModel: SwapButtonViewModel = hiltViewModel<DefaultSwapButtonViewModel>(),
    listener: SwapScreenListener
) {
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
                        buttonViewModel = buttonViewModel
                    )
                }
                SwapViewModel.ViewState.NoAccountState -> SwapNoAccountState(listener)
            }
        }
    }

    LaunchedEffect(Unit) {
        swapViewModel.initViewState()
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
    buttonViewModel: SwapButtonViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                configViewModel
            )
            SwapProviderWidget(
                modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
                providerViewModel
            )
        }
        SwapButtonWidget(buttonViewModel) { }
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

interface SwapScreenListener : SwapToolbarListener {
    fun onCreateAccountClick()
}

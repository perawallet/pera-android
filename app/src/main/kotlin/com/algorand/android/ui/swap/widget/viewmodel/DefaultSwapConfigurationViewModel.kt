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

package com.algorand.android.ui.swap.widget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.swap.widget.viewmodel.SwapConfigurationViewModel.ViewState
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHolding
import com.algorand.wallet.viewmodel.StateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private typealias AssetContentState = SwapAssetSelectionViewModel.ViewState.Content

@HiltViewModel
class DefaultSwapConfigurationViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountAssetHolding: GetAccountAssetHolding
) : ViewModel(), SwapConfigurationViewModel {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    override val state: StateFlow<ViewState>
        get() = stateDelegate.state

    override fun initViewState(
        addressFlow: Flow<String?>,
        assetInFlow: Flow<SwapAssetSelectionViewModel.ViewState>,
        assetOutFlow: Flow<SwapAssetSelectionViewModel.ViewState>
    ) {
        combine(addressFlow, assetInFlow, assetOutFlow, ::getViewState)
            .onEach { viewState -> stateDelegate.updateState { viewState } }
            .launchIn(viewModelScope)
    }

    private suspend fun getViewState(
        address: String?,
        assetInState: SwapAssetSelectionViewModel.ViewState,
        assetOutState: SwapAssetSelectionViewModel.ViewState
    ): ViewState {
        return when {
            assetInState is AssetContentState && assetOutState is AssetContentState && !address.isNullOrBlank() -> {
                val assetHolding = getAccountAssetHolding(address, assetOutState.assetDetail.assetId)
                val isSwitchButtonEnabled = assetHolding != null && assetHolding.amount > BigInteger.ZERO
                ViewState.Content(isSwitchButtonEnabled)
            }
            else -> ViewState.Idle
        }
    }
}

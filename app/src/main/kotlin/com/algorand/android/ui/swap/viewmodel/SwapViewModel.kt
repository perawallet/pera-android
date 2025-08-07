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

package com.algorand.android.ui.swap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.swap.viewmodel.SwapViewModel.ViewState
import com.algorand.wallet.asset.domain.usecase.GetUsdcAssetId
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.asset.domain.util.AssetConstants.USDC_TESTNET_ID
import com.algorand.wallet.swap.domain.usecase.GetPreselectedSwapAddress
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SwapViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getPreselectedSwapAddress: GetPreselectedSwapAddress,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getUsdcAssetId: GetUsdcAssetId
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    private val _addressFlow = MutableStateFlow<String?>(null)
    val addressFlow: StateFlow<String?>
        get() = _addressFlow.asStateFlow()

    private val _assetInFlow = MutableStateFlow<Long>(ALGO_ID)
    val assetInFlow: StateFlow<Long>
        get() = _assetInFlow.asStateFlow()

    private val _assetOutFlow = MutableStateFlow<Long>(USDC_TESTNET_ID)
    val assetOutFlow: StateFlow<Long>
        get() = _assetOutFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _assetOutFlow.value = getUsdcAssetId()
        }
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun switchAssets() {
        val assetInId = _assetInFlow.value
        val assetOutId = _assetOutFlow.value
        _assetInFlow.value = assetOutId
        _assetOutFlow.value = assetInId
    }

    fun initViewState() {
        stateDelegate.onState<ViewState.Idle> {
            viewModelScope.launch {
                val address = getPreselectedSwapAddress()
                if (address == null) {
                    stateDelegate.updateState { ViewState.NoAccountState }
                } else {
                    _addressFlow.value = address
                    val accountIcon = getAccountIconDrawablePreview(address)
                    val accountDisplayName = getAccountDisplayName(address)
                    stateDelegate.updateState {
                        ViewState.Content(accountIcon, accountDisplayName)
                    }
                }
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object NoAccountState : ViewState
        data class Content(
            val accountIconDrawable: AccountIconDrawablePreview,
            val accountDisplayName: AccountDisplayName
        ) : ViewState
    }
}

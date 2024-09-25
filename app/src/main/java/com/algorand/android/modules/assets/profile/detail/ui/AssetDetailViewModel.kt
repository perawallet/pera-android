/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.assets.profile.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.asset.detail.ui.detail.model.AssetDetailPreview
import com.algorand.android.module.asset.detail.ui.detail.usecase.GetAssetDetailPreviewFlow
import com.algorand.android.core.BaseViewModel
import com.algorand.android.foundation.Event
import com.algorand.android.modules.tracking.swap.assetdetail.AssetDetailAlgoSwapClickEventTracker
import com.algorand.android.module.swap.component.common.usecase.GetSwapNavigationDestination
import com.algorand.android.utils.ALGO_SHORT_NAME
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AssetDetailViewModel @Inject constructor(
    private val algoSwapClickEventTracker: AssetDetailAlgoSwapClickEventTracker,
    private val getAssetDetailPreviewFlow: GetAssetDetailPreviewFlow,
    private val getSwapNavigationDestination: GetSwapNavigationDestination,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val assetId = savedStateHandle.getOrThrow<Long>(ASSET_ID_KEY)
    val accountAddress = savedStateHandle.getOrThrow<String>(ACCOUNT_ADDRESS_KEY)
    private val isQuickActionButtonsVisible = savedStateHandle.getOrThrow<Boolean>(IS_QUICK_ACTION_BUTTONS_VISIBLE_KEY)

    private val _assetDetailPreviewFlow = MutableStateFlow<AssetDetailPreview?>(null)
    val assetDetailPreviewFlow: StateFlow<AssetDetailPreview?> get() = _assetDetailPreviewFlow

    val canAccountSignTransaction
        get() = _assetDetailPreviewFlow.value?.accountDetailSummary?.accountDetail?.canSignTransaction() ?: false

    init {
        initAssetDetailPreview()
    }

    fun onSwapClick() {
        viewModelScope.launchIO {
            if (assetId == ALGO_ASSET_ID) algoSwapClickEventTracker.logAlgoSwapClickEvent()
            _assetDetailPreviewFlow.update { preview ->
                preview?.copy(
                    swapNavigationDestinationEvent = Event(getSwapNavigationDestination(accountAddress))
                )
            }
        }
    }

    fun onMarketClick() {
        _assetDetailPreviewFlow.update { preview ->
            val safeTokenId = if (preview?.assetId == ALGO_ASSET_ID) ALGO_SHORT_NAME else preview?.assetId.toString()
            preview?.copy(
                navigateToDiscoverMarket = Event(safeTokenId)
            )
        }
    }

    private fun initAssetDetailPreview() {
        viewModelScope.launchIO {
            getAssetDetailPreviewFlow(accountAddress, assetId, isQuickActionButtonsVisible).collect {
                _assetDetailPreviewFlow.value = it
            }
        }
    }

    companion object {
        const val ASSET_ID_KEY = "assetId"
        const val ACCOUNT_ADDRESS_KEY = "accountAddress"
        const val IS_QUICK_ACTION_BUTTONS_VISIBLE_KEY = "isQuickActionButtonsVisible"
    }
}

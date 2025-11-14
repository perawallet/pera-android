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

package com.algorand.android.ui.asset.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.DisplayError
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToMeld
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToSwapV2
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewState
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewState.Content
import com.algorand.android.usecase.NetworkSlugUseCase
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO Rename after deleting asset detail v1
@HiltViewModel
class AssetDetailV2ViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAsset: GetAsset,
    private val fetchAsset: FetchAsset,
    private val getAccountType: GetAccountType,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val networkSlugUseCase: NetworkSlugUseCase
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState(address: String, assetId: Long) {
        stateDelegate.onState<ViewState.Idle> {
            loadViewState(address, assetId)
        }
    }

    fun reloadViewState() {
        stateDelegate.onState<ViewState.Error> {
            loadViewState(it.address, it.assetId)
        }
    }

    fun navigateToSwap() {
        stateDelegate.onState<Content> { content ->
            viewModelScope.launch {
                eventDelegate.sendEvent(viewModelScope, NavigateToSwapV2(content.address, content.asset.id))
            }
        }
    }

    fun navigateToOfframp() {
        stateDelegate.onState<Content> { content ->
            viewModelScope.launch {
                val canSignTransaction = getAccountType(content.address)?.canSignTransaction() == true
                if (canSignTransaction) {
                    eventDelegate.sendEvent(NavigateToMeld(content.address))
                } else {
                    eventDelegate.sendEvent(DisplayError(R.string.this_action_is_not_available))
                }
            }
        }
    }

    fun navigateToSend() {
        stateDelegate.onState<Content> { content ->
            viewModelScope.launch {
                val canSignTransaction = getAccountType(content.address)?.canSignTransaction() == true
                if (canSignTransaction) {
                    val assetTransaction = AssetTransaction(senderAddress = content.address, assetId = content.asset.id)
                    eventDelegate.sendEvent(ViewEvent.NavigateToSendNavigation(assetTransaction))
                } else {
                    eventDelegate.sendEvent(DisplayError(R.string.this_action_is_not_available))
                }
            }
        }
    }

    fun navigateToReceive() {
        stateDelegate.onState<Content> { content ->
            eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToShowQr(content.address))
        }
    }

    fun getActiveNodeSlug() = networkSlugUseCase.getActiveNodeSlug()

    private fun loadViewState(address: String, assetId: Long) {
        stateDelegate.updateState { ViewState.Loading }
        viewModelScope.launch {
            val asset = getAsset(assetId) ?: fetchAsset(assetId).getDataOrNull()
            val viewState = if (asset == null) {
                ViewState.Error(address, assetId)
            } else {
                Content(asset, address, getAccountDisplayName(address), getAccountIconDrawablePreview(address))
            }
            stateDelegate.updateState { viewState }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data class Error(val address: String, val assetId: Long) : ViewState
        data class Content(
            val asset: Asset,
            val address: String,
            val accountDisplayName: AccountDisplayName,
            val accountIconDrawable: AccountIconDrawablePreview
        ) : ViewState
    }

    sealed interface ViewEvent {
        data class NavigateToSwapV2(val address: String, val assetOutId: Long) : ViewEvent
        data class NavigateToSendNavigation(val assetTransaction: AssetTransaction) : ViewEvent
        data class NavigateToMeld(val address: String) : ViewEvent
        data class NavigateToShowQr(val address: String) : ViewEvent
        data class DisplayError(val errorResId: Int) : ViewEvent
    }
}

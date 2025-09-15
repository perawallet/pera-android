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
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewState
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewState.Content
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

// TODO Rename after deleting asset detail v1
@HiltViewModel
class AssetDetailV2ViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAsset: GetAsset,
    private val fetchAsset: FetchAsset
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

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
}

/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.send.assetselection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.algorand.android.mapper.AssetSelectionMapper
import com.algorand.android.models.AssetSelectionOptInPayload
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.accountsorting.domain.usecase.GetAssetCollectibleLiteSortType
import com.algorand.android.modules.assets.core.ui.domain.usecase.GetAssetName
import com.algorand.android.modules.parity.domain.usecase.GetSelectedCurrencyDetailFlow
import com.algorand.android.ui.asset.selection.view.model.BaseSelectAssetItem
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewEvent
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewState
import com.algorand.android.ui.send.assetselection.AssetSelectionViewModel.ViewState.Content.ContentStateType
import com.algorand.android.usecase.TransactionTipsUseCase
import com.algorand.android.utils.getOrThrow
import com.algorand.wallet.account.info.domain.usecase.IsAssetOptedInByAccount
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQueryFilter
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.asset.domain.usecase.GetAssetCollectibleLitesFlow
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class AssetSelectionViewModel @Inject constructor(
    private val getAssetCollectibleLiteSortType: GetAssetCollectibleLiteSortType,
    private val getSelectedCurrencyDetailFlow: GetSelectedCurrencyDetailFlow,
    private val getAssetCollectibleLitesFlow: GetAssetCollectibleLitesFlow,
    private val isAssetOptedInByAccount: IsAssetOptedInByAccount,
    private val getAsset: GetAsset,
    private val getAssetName: GetAssetName,
    private val transactionTipsUseCase: TransactionTipsUseCase,
    private val assetSelectionMapper: AssetSelectionMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    savedStateHandle: SavedStateHandle
) : ViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    val assetTransaction: AssetTransaction = savedStateHandle.getOrThrow(ASSET_TRANSACTION_KEY)

    private var assetSelectionListObservationJob: Job? = null

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState() {
        stateDelegate.onState<ViewState.Idle> {
            stateDelegate.updateState { ViewState.Loading }
        }
        observeAssetSelectionItems()
    }

    private fun observeAssetSelectionItems() {
        if (assetSelectionListObservationJob?.isActive == true) {
            assetSelectionListObservationJob?.cancel()
        }
        assetSelectionListObservationJob = viewModelScope.launch {
            combine(
                getAssetCollectibleLitesFlow(getAssetSelectionQuery()).cachedIn(this),
                getSelectedCurrencyDetailFlow()
            ) { assetLites, _ ->
                assetSelectionMapper.createAssetSelectionItems(assetLites)
            }.collectLatest {
                val state = ViewState.Content(it, ContentStateType.Idle)
                stateDelegate.updateState { state }
            }
        }
    }

    fun shouldShowTransactionTips(): Boolean {
        return transactionTipsUseCase.shouldShowTransactionTips()
    }

    fun updatePreviewWithSelectedAsset(assetId: Long) {
        stateDelegate.onState<ViewState.Content> { currentState ->
            assetSelectionListObservationJob?.cancel()
            val receiverAddress = assetTransaction.receiverUser?.publicKey
            if (receiverAddress != null) {
                checkIfReceiverOptedIn(currentState, assetId, receiverAddress)
            } else {
                eventDelegate.sendEvent(viewModelScope, ViewEvent.NavToAssetTransferAmountFragment(assetId))
            }
        }
    }

    private fun checkIfReceiverOptedIn(currentState: ViewState.Content, assetId: Long, receiverAddress: String) {
        stateDelegate.updateState { currentState.copy(type = ContentStateType.Loading) }
        viewModelScope.launch {
            if (isReceiverOptedInToAsset(assetId, receiverAddress)) {
                stateDelegate.updateState { currentState.copy(type = ContentStateType.Idle) }
                eventDelegate.sendEvent(ViewEvent.NavToAssetTransferAmountFragment(assetId))
            } else {
                stateDelegate.updateState { currentState.copy(type = ContentStateType.Idle) }
                val optInPayload = getOptInPayload(assetId) ?: return@launch
                eventDelegate.sendEvent(ViewEvent.NavToOptIn(optInPayload))
            }
        }
    }

    private suspend fun isReceiverOptedInToAsset(assetId: Long, address: String): Boolean {
        return assetId == ALGO_ID || isAssetOptedInByAccount(address, assetId)
    }

    private suspend fun getOptInPayload(assetId: Long): AssetSelectionOptInPayload? {
        val receiverAddress = assetTransaction.receiverUser?.publicKey ?: return null
        val assetDetail = getAsset(assetId) ?: return null
        return AssetSelectionOptInPayload(
            assetId = assetId,
            senderAddress = assetTransaction.senderAddress,
            receiverAddress = receiverAddress,
            assetName = getAssetName(assetDetail.fullName).assetName,
            assetAmount = assetTransaction.amount
        )
    }

    private suspend fun getAssetSelectionQuery(): AssetCollectibleLiteQuery {
        val filters = listOf(AssetCollectibleLiteQueryFilter.FilterOutCollectiblesWithZeroAmount)
        return AssetCollectibleLiteQuery(
            addresses = listOf(assetTransaction.senderAddress),
            sortType = getAssetCollectibleLiteSortType(),
            filters = filters
        )
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState

        data class Content(
            val assetListItems: PagingData<BaseSelectAssetItem>,
            val type: ContentStateType
        ) : ViewState {
            sealed interface ContentStateType {
                data object Idle : ContentStateType
                data object Loading : ContentStateType
            }
        }
    }

    sealed interface ViewEvent {
        data class NavToAssetTransferAmountFragment(val assetId: Long) : ViewEvent
        data class NavToOptIn(val payload: AssetSelectionOptInPayload) : ViewEvent
    }

    companion object {
        private const val ASSET_TRANSACTION_KEY = "assetTransaction"
    }
}

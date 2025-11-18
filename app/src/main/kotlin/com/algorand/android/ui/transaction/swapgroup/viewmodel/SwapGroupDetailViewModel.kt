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

package com.algorand.android.ui.transaction.swapgroup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.ui.transaction.history.mapper.TransactionHistoryItemMapper
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem
import com.algorand.android.ui.transaction.swapgroup.viewmodel.SwapGroupDetailViewModel.ViewState
import com.algorand.android.ui.transaction.swapgroup.viewmodel.SwapGroupDetailViewModel.ViewState.Content.ContentState.Error
import com.algorand.android.ui.transaction.swapgroup.viewmodel.SwapGroupDetailViewModel.ViewState.Content.ContentState.Loading
import com.algorand.android.ui.transaction.swapgroup.viewmodel.SwapGroupDetailViewModel.ViewState.Content.ContentState.Success
import com.algorand.wallet.transaction.history.domain.model.TransactionHistorySwapGroupDetail
import com.algorand.wallet.transaction.history.domain.usecase.GetTransactionHistorySwapGroupDetail
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwapGroupDetailViewModel @Inject constructor(
    private val getSwapGroupDetail: GetTransactionHistorySwapGroupDetail,
    private val transactionHistoryItemMapper: TransactionHistoryItemMapper,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initTransactions(address: String, groupId: String) {
        stateDelegate.onState<ViewState.Idle> {
            loadSwapGroupTransactions(address, groupId)
        }
    }

    fun retryInitTransactions() {
        stateDelegate.onState<ViewState.Content> { contentState ->
            loadSwapGroupTransactions(contentState.address, contentState.groupId)
        }
    }

    private fun loadSwapGroupTransactions(address: String, groupId: String) {
        viewModelScope.launch {
            stateDelegate.updateState { ViewState.Content(address, groupId, Loading) }
            val newState = getSwapGroupDetail(address, groupId).use(
                onSuccess = { detail ->
                    ViewState.Content(address, groupId, Success(mapToHistoryItems(detail)))
                },
                onFailed = { _, _ -> ViewState.Content(address, groupId, Error) }
            )
            stateDelegate.updateState { newState }
        }
    }

    private fun mapToHistoryItems(detail: TransactionHistorySwapGroupDetail): List<TransactionHistoryItem> {
        val historyItems = mutableListOf<TransactionHistoryItem>()
        detail.transactions.forEachIndexed { index, transaction ->
            historyItems.add(transactionHistoryItemMapper.map(transaction))
            if (index < detail.transactions.size - 1) {
                historyItems.add(TransactionHistoryItem.Separator)
            }
        }
        return historyItems
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val address: String,
            val groupId: String,
            val state: ContentState
        ) : ViewState {
            sealed interface ContentState {
                data object Loading : ContentState
                data object Error : ContentState
                data class Success(val transactionHistoryItems: List<TransactionHistoryItem>) : ContentState
            }
        }
    }
}

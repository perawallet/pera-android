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

package com.algorand.android.ui.transaction.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.algorand.android.models.DateFilter
import com.algorand.android.models.DateRange
import com.algorand.android.ui.transaction.history.mapper.TransactionHistoryItemMapper
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem.Date
import com.algorand.android.ui.transaction.history.viewmodel.TransactionHistoryViewModel.ViewState
import com.algorand.android.utils.formatAsDate
import com.algorand.android.utils.formatAsRFC3339Version
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistoryPagingData
import com.algorand.wallet.transaction.history.domain.usecase.GetTransactionHistory
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val getTransactionHistory: GetTransactionHistory,
    private val transactionHistoryItemMapper: TransactionHistoryItemMapper,
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    private val dateFilter = MutableStateFlow<DateFilter>(DateFilter.AllTime)

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun initViewState(address: String, assetId: Long) {
        stateDelegate.onState<ViewState.Idle> {
            stateDelegate.updateState {
                ViewState.Content(address, assetId, getTransactionPagingDataFlow(address, assetId))
            }
        }
    }

    fun isFilterSelected(): Boolean = dateFilter.value != DateFilter.AllTime

    fun getSelectedDateRange(): DateRange? = dateFilter.value.getDateRange()

    fun setDateFilter(dateFilter: DateFilter?) {
        if (dateFilter != null) this.dateFilter.value = dateFilter
    }

    fun getDateFilter(): DateFilter = dateFilter.value

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getTransactionPagingDataFlow(address: String, assetId: Long): Flow<PagingData<TransactionHistoryItem>> {
        return dateFilter
            .flatMapLatest { filter ->
                val pagingData = getPagingData(address, assetId, filter)
                getTransactionHistory(pagingData).map(::mapToListItems)
            }
            .cachedIn(viewModelScope)
            .flowOn(Dispatchers.IO)
    }

    private fun mapToListItems(pagingData: PagingData<TransactionHistory>): PagingData<TransactionHistoryItem> {
        return pagingData.map { transactionHistory ->
            PaginationMediatorItem(transactionHistory, transactionHistoryItemMapper.map(transactionHistory))
        }.insertSeparators { firstItem, secondItem ->
            when {
                shouldAddDateSeparator(firstItem?.history, secondItem?.history) -> {
                    val formattedDate = secondItem?.history?.time?.formatAsDate().orEmpty()
                    PaginationMediatorItem(history = null, historyItem = Date(formattedDate))
                }

                firstItem?.history != null && secondItem?.history != null -> {
                    PaginationMediatorItem(history = null, historyItem = TransactionHistoryItem.Separator)
                }

                else -> null
            }
        }.map { it.historyItem }
    }

    private fun shouldAddDateSeparator(firstTxn: TransactionHistory?, secondTxn: TransactionHistory?): Boolean {
        return when {
            secondTxn == null -> false
            firstTxn == null -> true
            firstTxn.time.toLocalDate() == secondTxn.time.toLocalDate() -> false
            else -> true
        }
    }

    private fun getPagingData(address: String, assetId: Long, filter: DateFilter?): TransactionHistoryPagingData {
        return TransactionHistoryPagingData(
            address = address,
            assetId = assetId,
            afterTime = filter?.getDateRange()?.from?.formatAsRFC3339Version(),
            beforeTime = filter?.getDateRange()?.to?.formatAsRFC3339Version(),
            itemPerPage = TRANSACTIONS_PAGE_SIZE
        )
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class Content(
            val address: String,
            val assetId: Long,
            val pagingData: Flow<PagingData<TransactionHistoryItem>>
        ) : ViewState
    }

    private data class PaginationMediatorItem(
        val history: TransactionHistory?,
        val historyItem: TransactionHistoryItem
    )

    private companion object {
        const val TRANSACTIONS_PAGE_SIZE = 25
    }
}

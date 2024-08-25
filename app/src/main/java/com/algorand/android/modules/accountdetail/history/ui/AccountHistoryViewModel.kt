/*
 * Copyright 2022 Pera Wallet, LDA
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

package com.algorand.android.modules.accountdetail.history.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import com.algorand.android.core.component.domain.usecase.GetAccountTotalValueFlow
import com.algorand.android.dateui.mapper.DateFilterPreviewMapper
import com.algorand.android.dateui.model.DateFilter
import com.algorand.android.dateui.model.DateFilterPreview
import com.algorand.android.modules.tracking.accountdetail.accounthistory.AccountHistoryFragmentEventTracker
import com.algorand.android.modules.transaction.csv.ui.model.CsvStatusPreview
import com.algorand.android.modules.transaction.csv.ui.usecase.CsvStatusPreviewUseCase
import com.algorand.android.transactionhistoryui.TransactionHistoryPreviewManager
import com.algorand.android.transactionhistoryui.mapper.TransactionLoadStatePreviewMapper
import com.algorand.android.transactionhistoryui.model.BaseTransactionItem
import com.algorand.android.transactionhistoryui.model.TransactionLoadStatePreview
import com.algorand.android.transactionhistoryui.pendingtxn.domain.usecase.GetPendingTransactionItems
import com.algorand.android.utils.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class AccountHistoryViewModel @Inject constructor(
    private val transactionHistoryPreviewManager: TransactionHistoryPreviewManager,
    private val csvStatusPreviewUseCase: CsvStatusPreviewUseCase,
    private val accountHistoryFragmentEventTracker: AccountHistoryFragmentEventTracker,
    private val getAccountTotalValueFlow: GetAccountTotalValueFlow,
    private val dateFilterPreviewMapper: DateFilterPreviewMapper,
    private val transactionLoadStatePreviewMapper: TransactionLoadStatePreviewMapper,
    private val getPendingTransactionItems: GetPendingTransactionItems,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dateFilterFlow = MutableStateFlow<DateFilter>(DateFilter.AllTime)

    private val _dateFilterPreviewFlow = MutableStateFlow(getDefaultDateFilterPreview())
    val dateFilterPreviewFlow: Flow<DateFilterPreview>
        get() = _dateFilterPreviewFlow

    private var pendingTransactionPolling: Job? = null

    private val _pendingTransactionsFlow = MutableStateFlow<List<BaseTransactionItem>?>(null)
    val pendingTransactionsFlow: Flow<List<BaseTransactionItem>?>
        get() = _pendingTransactionsFlow
            .distinctUntilChanged(getPendingTransactionItems.pendingFlowDistinctUntilChangedListener)

    val csvStatusPreview: Flow<CsvStatusPreview?>
        get() = _csvStatusPreviewFlow
    private val _csvStatusPreviewFlow = MutableStateFlow<CsvStatusPreview?>(null)

    val accountAddress = savedStateHandle.getOrThrow<String>(PUBLIC_KEY)

    init {
        startAccountBalanceFlow()
        initDateFilterFlow()
    }

    fun getDateFilterValue(): DateFilter {
        return dateFilterFlow.value
    }

    fun setDateFilter(dateFilter: DateFilter) {
        viewModelScope.launch {
            dateFilterFlow.emit(dateFilter)
            _dateFilterPreviewFlow.emit(dateFilterPreviewMapper(dateFilter))
        }
    }

    fun getAccountHistoryFlow(): Flow<PagingData<BaseTransactionItem>>? {
        return transactionHistoryPreviewManager.getTransactionHistoryPaginationFlow(accountAddress, viewModelScope)
    }

    fun activatePendingTransaction() {
        activatePendingTransactionsPolling()
    }

    fun deactivatePendingTransaction() {
        pendingTransactionPolling?.cancel()
    }

    fun createTransactionLoadStatePreview(
        combinedLoadStates: CombinedLoadStates,
        itemCount: Int,
        isLastStateError: Boolean
    ): TransactionLoadStatePreview {
        return transactionLoadStatePreviewMapper(combinedLoadStates, itemCount, isLastStateError)
    }

    fun refreshTransactionHistory() {
        transactionHistoryPreviewManager.refreshTransactionHistory()
    }

    fun createCsvFile(cacheDirectory: File) {
        viewModelScope.launch {
            val dateRange = getDateFilterValue().getDateRange()
            csvStatusPreviewUseCase.createCsvFile(
                cacheDir = cacheDirectory,
                dateRange = dateRange,
                publicKey = accountAddress
            ).collectLatest {
                _csvStatusPreviewFlow.emit(it)
            }
        }
    }

    private fun initDateFilterFlow() {
        dateFilterFlow
            .onEach { transactionHistoryPreviewManager.filterHistoryByDate(it) }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private fun refreshAccountHistoryData() {
        transactionHistoryPreviewManager.refreshTransactionHistory()
    }

    private fun activatePendingTransactionsPolling() {
        pendingTransactionPolling = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val pendingTransactions = getPendingTransactionItems(accountAddress, null)
                synchronized(_pendingTransactionsFlow) {
                    _pendingTransactionsFlow.value = pendingTransactions.getDataOrNull().orEmpty()
                }
                delay(PENDING_TRANSACTION_DELAY)
            }
        }
    }

    private fun getDefaultDateFilterPreview(): DateFilterPreview {
        return dateFilterPreviewMapper(DateFilter.AllTime)
    }

    private fun startAccountBalanceFlow() {
        viewModelScope.launch {
            getAccountTotalValueFlow(accountAddress, true).collectLatest {
                refreshAccountHistoryData()
            }
        }
    }

    fun logAccountHistoryFilterEventTracker() {
        viewModelScope.launch {
            accountHistoryFragmentEventTracker.logAccountHistoryFilterEvent()
        }
    }

    fun logAccountHistoryExportCsvEventTracker() {
        viewModelScope.launch {
            accountHistoryFragmentEventTracker.logAccountHistoryExportCsvEvent()
        }
    }

    companion object {
        const val PUBLIC_KEY = "public_key"
        private const val PENDING_TRANSACTION_DELAY = 800L
    }
}

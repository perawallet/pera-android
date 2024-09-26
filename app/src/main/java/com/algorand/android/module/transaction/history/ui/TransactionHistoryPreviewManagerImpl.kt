/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.transaction.history.ui

import androidx.paging.PagingData
import androidx.paging.map
import com.algorand.android.module.date.ui.model.DateFilter
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem
import com.algorand.android.module.transaction.history.component.domain.usecase.GetTransactionPaginationFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class TransactionHistoryPreviewManagerImpl @Inject constructor(
    private val getTransactionPaginationFlow: GetTransactionPaginationFlow,
    private val transactionHistoryProcessor: TransactionHistoryProcessor
) : TransactionHistoryPreviewManager {

    override fun getTransactionHistoryPaginationFlow(
        publicKey: String,
        coroutineScope: CoroutineScope,
        assetId: Long?,
        txnType: String?
    ): Flow<PagingData<BaseTransactionItem>>? {
        return getTransactionPaginationFlow(publicKey, assetId, coroutineScope, txnType)?.map { pagingData ->
            pagingData.map { transaction ->
                transactionHistoryProcessor(publicKey, transaction)
            }
        }
    }

    override suspend fun filterHistoryByDate(dateFilter: DateFilter) {
        val dateRange = dateFilter.getDateRange()
        getTransactionPaginationFlow.filterHistoryByDate(dateRange?.from, dateRange?.to)
    }

    override fun refreshTransactionHistory() {
        getTransactionPaginationFlow.refreshTransactionHistory()
    }
}

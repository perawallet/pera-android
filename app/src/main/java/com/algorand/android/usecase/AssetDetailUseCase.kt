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

package com.algorand.android.usecase

import androidx.paging.PagingData
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.core.BaseUseCase
import com.algorand.android.dateui.mapper.DateFilterPreviewMapper
import com.algorand.android.dateui.model.DateFilter
import com.algorand.android.dateui.model.DateFilterPreview
import com.algorand.android.transaction.pendingtxn.domain.model.TransactionType
import com.algorand.android.transactionhistoryui.TransactionHistoryPreviewManager
import com.algorand.android.transactionhistoryui.model.BaseTransactionItem
import com.algorand.android.transactionhistoryui.pendingtxn.domain.usecase.GetPendingTransactionItems
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@SuppressWarnings("LongParameterList")
class AssetDetailUseCase @Inject constructor(
    private val transactionHistoryPreviewManager: TransactionHistoryPreviewManager,
    private val getPendingTransactionItems: GetPendingTransactionItems,
    private val dateFilterPreviewMapper: DateFilterPreviewMapper
) : BaseUseCase() {

    val pendingTransactionDistinctUntilChangedListener
        get() = getPendingTransactionItems.pendingFlowDistinctUntilChangedListener

    fun getTransactionFlow(
        publicKey: String,
        assetIdFilter: Long,
        cacheInScope: CoroutineScope
    ): Flow<PagingData<BaseTransactionItem>>? {
        return transactionHistoryPreviewManager.getTransactionHistoryPaginationFlow(
            publicKey,
            cacheInScope,
            assetIdFilter,
            TransactionType.PAY_TRANSACTION.takeIf { assetIdFilter == ALGO_ASSET_ID }?.value
        )
    }

    suspend fun setDateFilter(dateFilter: DateFilter) {
        transactionHistoryPreviewManager.filterHistoryByDate(dateFilter)
    }

    fun createDateFilterPreview(dateFilter: DateFilter): DateFilterPreview {
        return dateFilterPreviewMapper(dateFilter)
    }

    fun refreshTransactionHistory() {
        transactionHistoryPreviewManager.refreshTransactionHistory()
    }

    suspend fun fetchPendingTransactions(publicKey: String, assetId: Long): List<BaseTransactionItem> {
        return getPendingTransactionItems(publicKey, assetId).getDataOrNull().orEmpty()
    }
}

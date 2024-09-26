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
import com.algorand.android.module.date.ui.model.DateFilter
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface TransactionHistoryPreviewManager {

    fun getTransactionHistoryPaginationFlow(
        publicKey: String,
        coroutineScope: CoroutineScope,
        assetId: Long? = null,
        txnType: String? = null
    ): Flow<PagingData<BaseTransactionItem>>?

    suspend fun filterHistoryByDate(dateFilter: DateFilter)

    fun refreshTransactionHistory()
}

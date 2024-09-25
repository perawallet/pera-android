package com.algorand.android.transaction_history_component.domain.usecase

import androidx.paging.PagingData
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface GetTransactionPaginationFlow {
    operator fun invoke(
        address: String,
        assetIdFilter: Long? = null,
        coroutineScope: CoroutineScope,
        txnType: String? = null
    ): Flow<PagingData<BaseTransactionHistoryItem>>?

    suspend fun filterHistoryByDate(fromDate: ZonedDateTime?, toDate: ZonedDateTime?)

    fun refreshTransactionHistory()
}
package com.algorand.android.module.transaction.history.component.domain.usecase

import androidx.paging.PagingData
import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem
import java.time.ZonedDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

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

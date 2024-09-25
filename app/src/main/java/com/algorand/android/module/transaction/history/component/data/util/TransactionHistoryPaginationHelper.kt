package com.algorand.android.module.transaction.history.component.data.util

import androidx.paging.*
import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.module.transaction.history.component.domain.utils.TransactionHistoryConstants.DEFAULT_TRANSACTION_TO_FETCH_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class TransactionHistoryPaginationHelper @Inject constructor() {

    private var transactionHistoryDataSource: TransactionHistoryDataSource? = null

    private val pagerConfig = PagingConfig(pageSize = DEFAULT_TRANSACTION_TO_FETCH_COUNT)

    var transactionPaginationFlow: SharedFlow<PagingData<BaseTransactionHistory>>? = null
        private set

    fun fetchTransactionHistory(
        scope: CoroutineScope,
        onLoad: suspend (nextKey: String?) -> PagingSource.LoadResult<String, BaseTransactionHistory>
    ) {
        if (transactionPaginationFlow == null) {
            transactionPaginationFlow = Pager(pagerConfig) {
                TransactionHistoryDataSource(onLoad).also { transactionHistoryDataSource = it }
            }.flow
                .cachedIn(scope)
                .shareIn(scope, SharingStarted.Lazily)
        } else {
            refreshTransactionHistoryData()
        }
    }

    fun refreshTransactionHistoryData() {
        transactionHistoryDataSource?.invalidate()
    }
}

package com.algorand.android.transaction_history_component.data.util

import com.algorand.android.pagination.component.PeraPagingSource
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import javax.inject.Inject

class TransactionHistoryDataSource @Inject constructor(
    private val onLoad: suspend (nextKey: String?) -> LoadResult<String, BaseTransactionHistory>
) : PeraPagingSource<String, BaseTransactionHistory>() {

    override val logTag: String = TransactionHistoryDataSource::class.java.simpleName

    override suspend fun initializeData(): LoadResult<String, BaseTransactionHistory> {
        return onLoad(null)
    }

    override suspend fun loadMore(loadUrl: String): LoadResult<String, BaseTransactionHistory> {
        return onLoad(loadUrl)
    }
}

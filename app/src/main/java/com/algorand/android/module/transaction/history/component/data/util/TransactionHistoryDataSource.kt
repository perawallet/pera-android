package com.algorand.android.module.transaction.history.component.data.util

import com.algorand.android.module.transaction.history.component.domain.model.BaseTransactionHistoryItem.BaseTransactionHistory
import com.algorand.android.utils.PeraPagingSource
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

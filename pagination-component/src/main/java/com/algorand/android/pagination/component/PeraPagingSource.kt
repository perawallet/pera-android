package com.algorand.android.pagination.component

import androidx.paging.*
import kotlinx.coroutines.*

abstract class PeraPagingSource<PagingKey : Any, PagingValue : Any> : PagingSource<PagingKey, PagingValue>() {

    /**
     * Paging library checks loadUrl and nextUrl after fetching the data.
     * So, we can not solve `IllegalStateException` that was caused by fetching the same data twice.
     * To solve this issue, we enable `key reuse` and check manually if data is fetched twice or not.
     */
    override val keyReuseSupported: Boolean
        get() = true

    protected abstract val logTag: String

    protected abstract suspend fun initializeData(): LoadResult<PagingKey, PagingValue>
    protected abstract suspend fun loadMore(loadUrl: PagingKey): LoadResult<PagingKey, PagingValue>

    override suspend fun load(params: LoadParams<PagingKey>): LoadResult<PagingKey, PagingValue> {
        return withContext(Dispatchers.IO) {
            if (params is LoadParams.Refresh) {
                initializeData()
            } else {

                val urlToFetchData = params.key ?: return@withContext LoadResult.Error(EndOfPagingDataException())

                loadMore(urlToFetchData).let { loadResult ->
                    if (areCurrentUrlAndNextUrlTheSame(urlToFetchData, loadResult)) {
                        // TODO sendErrorLog("$logTag nextUrl is the same with previous url")
                        return@withContext LoadResult.Error(EndOfPagingDataException())
                    } else {
                        loadResult
                    }
                }
            }
        }
    }

    private fun areCurrentUrlAndNextUrlTheSame(
        latestUrlToFetchData: PagingKey,
        loadResult: LoadResult<PagingKey, PagingValue>
    ): Boolean {
        val nextUrlToFetchData = (loadResult as? LoadResult.Page)?.nextKey
        return latestUrlToFetchData == nextUrlToFetchData
    }

    override fun getRefreshKey(state: PagingState<PagingKey, PagingValue>): PagingKey? {
        return null
    }
}

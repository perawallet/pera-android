/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.transaction.history.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.transaction.history.data.mapper.TransactionHistoryMapper
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryResponse
import com.algorand.wallet.transaction.history.data.service.TransactionHistoryApiService
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistoryPagingData
import javax.inject.Inject

internal class DefaultTransactionHistoryPagingSource @Inject constructor(
    private val transactionHistoryApiService: TransactionHistoryApiService,
    private val transactionHistoryMapper: TransactionHistoryMapper
) : PagingSource<TransactionHistoryPagingData, TransactionHistory>() {

    override fun getRefreshKey(
        state: PagingState<TransactionHistoryPagingData, TransactionHistory>
    ): TransactionHistoryPagingData? = null

    override suspend fun load(
        params: LoadParams<TransactionHistoryPagingData>
    ): LoadResult<TransactionHistoryPagingData, TransactionHistory> {
        return try {
            params.key
            when (params) {
                is LoadParams.Prepend -> prepend(params)
                is LoadParams.Append -> append(params)
                is LoadParams.Refresh -> {
                    val historyResult = getTransactionHistory(params.key)
                    getPageResult(params.key, historyResult)
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun append(
        params: LoadParams.Append<TransactionHistoryPagingData>
    ): LoadResult<TransactionHistoryPagingData, TransactionHistory> {
        val nextUrl = params.key.nextUrl ?: return emptyPage()
        val historyResult = getTransactionHistoryMore(nextUrl)
        if (historyResult.getDataOrNull()?.nextUrl == nextUrl) return emptyPage()
        return getPageResult(params.key, historyResult)
    }

    private suspend fun prepend(
        params: LoadParams.Prepend<TransactionHistoryPagingData>
    ): LoadResult<TransactionHistoryPagingData, TransactionHistory> {
        val previousUrl = params.key.previousUrl ?: return emptyPage()
        val historyResult = getTransactionHistoryMore(previousUrl)
        if (historyResult.getDataOrNull()?.previousUrl == previousUrl) return emptyPage()
        return getPageResult(params.key, historyResult)
    }

    private suspend fun getPageResult(
        params: TransactionHistoryPagingData?,
        historyResult: PeraResult<TransactionHistoryResponse>
    ): LoadResult<TransactionHistoryPagingData, TransactionHistory> {
        return historyResult.use(
            onSuccess = {
                LoadResult.Page(
                    data = it.transactions.mapNotNull { transactionHistoryMapper(params?.address.orEmpty(), it) },
                    prevKey = if (it.previousUrl != null) params?.copy(previousUrl = it.previousUrl) else null,
                    nextKey = if (it.nextUrl != null) params?.copy(nextUrl = it.nextUrl) else null
                )
            },
            onFailed = { exception, _ ->
                LoadResult.Error(exception)
            }
        )
    }

    private suspend fun getTransactionHistory(
        params: TransactionHistoryPagingData?
    ): PeraResult<TransactionHistoryResponse> {
        return try {
            val result = transactionHistoryApiService.getTransactionHistory(
                accountAddress = params?.address.orEmpty(),
                assetId = params?.assetId,
                afterTime = params?.afterTime,
                beforeTime = params?.beforeTime,
                limit = ITEM_PER_PAGE
            )
            PeraResult.Success(result)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    private suspend fun getTransactionHistoryMore(url: String): PeraResult<TransactionHistoryResponse> {
        return try {
            val result = transactionHistoryApiService.getTransactionHistoryMore(url)
            PeraResult.Success(result)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    private fun emptyPage(): LoadResult<TransactionHistoryPagingData, TransactionHistory> {
        return LoadResult.Page(emptyList(), null, null)
    }

    companion object {
        const val ITEM_PER_PAGE = 25
    }
}

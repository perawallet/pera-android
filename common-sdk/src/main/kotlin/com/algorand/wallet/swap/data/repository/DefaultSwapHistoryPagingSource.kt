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

package com.algorand.wallet.swap.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.data.mapper.SwapHistoryMapper
import com.algorand.wallet.swap.data.mapper.SwapHistoryStatusMapper
import com.algorand.wallet.swap.data.model.SwapHistoriesResponse
import com.algorand.wallet.swap.data.service.SwapApiService
import com.algorand.wallet.swap.domain.model.SwapHistory
import com.algorand.wallet.swap.domain.model.SwapHistoryPagingData
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus
import javax.inject.Inject

internal class DefaultSwapHistoryPagingSource @Inject constructor(
    private val swapApiService: SwapApiService,
    private val swapHistoryMapper: SwapHistoryMapper,
    private val swapHistoryStatusMapper: SwapHistoryStatusMapper
) : PagingSource<SwapHistoryPagingData, SwapHistory>() {

    override fun getRefreshKey(
        state: PagingState<SwapHistoryPagingData, SwapHistory>
    ): SwapHistoryPagingData? = null

    override suspend fun load(
        params: LoadParams<SwapHistoryPagingData>
    ): LoadResult<SwapHistoryPagingData, SwapHistory> {
        return try {
            val keys = params.key
            when (params) {
                is LoadParams.Refresh -> {
                    val swapHistoryResponseResult = getSwapHistory(params.key)
                    getPageResult(keys, swapHistoryResponseResult)
                }

                is LoadParams.Append -> append(params)
                is LoadParams.Prepend -> prepend(params)
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun append(
        params: LoadParams.Append<SwapHistoryPagingData>
    ): LoadResult<SwapHistoryPagingData, SwapHistory> {
        val nextUrl = params.key.nextUrl ?: return emptyPage()
        val response = getSwapHistoryMore(nextUrl)
        if (response.getDataOrNull()?.next == nextUrl) return emptyPage()
        return getPageResult(params.key, response)
    }

    private suspend fun prepend(
        params: LoadParams.Prepend<SwapHistoryPagingData>
    ): LoadResult<SwapHistoryPagingData, SwapHistory> {
        val previousUrl = params.key.previousUrl ?: return emptyPage()
        val response = getSwapHistoryMore(previousUrl)
        if (response.getDataOrNull()?.previous == previousUrl) return emptyPage()
        return getPageResult(params.key, response)
    }

    private suspend fun getPageResult(
        params: SwapHistoryPagingData?,
        historyResult: PeraResult<SwapHistoriesResponse>
    ): LoadResult<SwapHistoryPagingData, SwapHistory> {
        return historyResult.use(
            onSuccess = {
                LoadResult.Page(
                    data = it.results.mapNotNull { swapHistoryMapper(it) },
                    prevKey = if (it.previous != null) params?.copy(previousUrl = it.previous) else null,
                    nextKey = if (it.next != null) params?.copy(nextUrl = it.next) else null
                )
            },
            onFailed = { exception, _ ->
                LoadResult.Error(exception)
            }
        )
    }

    private suspend fun getSwapHistory(params: SwapHistoryPagingData?): PeraResult<SwapHistoriesResponse> {
        return try {
            val result = swapApiService.getSwapHistory(
                address = params?.address.orEmpty(),
                cursor = params?.nextUrl.orEmpty(),
                limit = ITEM_PER_PAGE,
                statuses = getSwapStatusString(params?.statuses.orEmpty())
            )
            PeraResult.Success(result)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private suspend fun getSwapHistoryMore(url: String): PeraResult<SwapHistoriesResponse> {
        return try {
            val result = swapApiService.getSwapHistoryMore(url)
            PeraResult.Success(result)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private fun getSwapStatusString(statuses: List<SwapHistoryStatus>): String {
        return statuses.joinToString(",") { status ->
            swapHistoryStatusMapper(status).value
        }
    }

    private fun emptyPage(): LoadResult<SwapHistoryPagingData, SwapHistory> = LoadResult.Page(emptyList(), null, null)

    companion object {
        const val ITEM_PER_PAGE = 20
    }
}

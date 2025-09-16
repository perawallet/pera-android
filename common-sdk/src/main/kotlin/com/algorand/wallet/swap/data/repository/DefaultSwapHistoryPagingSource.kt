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
            val swapHistoryResponseResult = getSwapHistory(params.key)
            if (params is LoadParams.Refresh) {
                getPageResult(keys, swapHistoryResponseResult)
            } else {
                if (swapHistoryResponseResult.getDataOrNull()?.next == keys?.nextUrl) {
                    return LoadResult.Error(Exception())
                }
                getPageResult(keys, swapHistoryResponseResult)
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun getPageResult(
        params: SwapHistoryPagingData?,
        historyResult: PeraResult<SwapHistoriesResponse>
    ): LoadResult<SwapHistoryPagingData, SwapHistory> {
        return historyResult.use(
            onSuccess = {
                LoadResult.Page(
                    data = it.results.mapNotNull { swapHistoryMapper(it) },
                    prevKey = params?.copy(previousUrl = it.previous),
                    nextKey = params?.copy(nextUrl = it.next)
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

    private fun getSwapStatusString(statuses: List<SwapHistoryStatus>): String {
        return statuses.joinToString(",") { status ->
            swapHistoryStatusMapper(status).value
        }
    }

    companion object {
        const val ITEM_PER_PAGE = 20
    }
}

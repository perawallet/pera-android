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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.data.mapper.SwapHistoryStatusMapper
import com.algorand.wallet.swap.data.mapper.SwapPairHistoryMapper
import com.algorand.wallet.swap.data.repository.DefaultSwapHistoryPagingSource.Companion.ITEM_PER_PAGE
import com.algorand.wallet.swap.data.service.SwapApiService
import com.algorand.wallet.swap.domain.model.SwapHistory
import com.algorand.wallet.swap.domain.model.SwapHistoryPagingData
import com.algorand.wallet.swap.domain.model.SwapHistoryStatus
import com.algorand.wallet.swap.domain.model.SwapPairHistory
import com.algorand.wallet.swap.domain.repository.SwapHistoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class DefaultSwapHistoryRepository @Inject constructor(
    private val swapHistoryPagingSource: PagingSource<SwapHistoryPagingData, SwapHistory>,
    private val swapApiService: SwapApiService,
    private val swapPairHistoryMapper: SwapPairHistoryMapper,
    private val swapHistoryStatusMapper: SwapHistoryStatusMapper
) : SwapHistoryRepository {

    override fun getSwapHistory(pagingData: SwapHistoryPagingData): Flow<PagingData<SwapHistory>> {
        return Pager(
            config = PagingConfig(pageSize = ITEM_PER_PAGE),
            initialKey = pagingData,
            pagingSourceFactory = { swapHistoryPagingSource }
        ).flow
    }

    override suspend fun getSwapPairHistory(
        address: String,
        statuses: List<SwapHistoryStatus>
    ): PeraResult<List<SwapPairHistory>> {
        return try {
            val statusesCsv = statuses.joinToString(",") { swapHistoryStatusMapper(it).value }
            val response = swapApiService.getSwapPairsHistory(address, statusesCsv).results
            val swapPairHistoryList = response.mapNotNull { swapPairHistoryMapper(it) }
            PeraResult.Success(swapPairHistoryList)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }
}

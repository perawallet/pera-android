/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.block.data.repository

import com.algorand.wallet.block.data.model.ShouldRefreshRequestBody
import com.algorand.wallet.block.data.service.BlockPollingApiService
import com.algorand.wallet.block.domain.repository.BlockPollingRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.CacheResult
import com.algorand.wallet.foundation.cache.SingleInMemoryLocalCache
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import com.algorand.wallet.foundation.network.utils.requestWithHipoErrorHandler

internal class BlockPollingRepositoryImpl(
    private val blockPollingApiService: BlockPollingApiService,
    private val blockPollingLocalCache: SingleInMemoryLocalCache<Long>,
    private val peraErrorHandler: PeraRetrofitErrorHandler
) : BlockPollingRepository {

    override suspend fun clearLastKnownBlockNumber() {
        blockPollingLocalCache.clear()
    }

    override suspend fun updateLastKnownBlockNumber(blockNumber: Long) {
        blockPollingLocalCache.put(CacheResult.Success.create(blockNumber))
    }

    override suspend fun getLastKnownAccountBlockNumber(): Long? {
        return blockPollingLocalCache.getOrNull()?.getDataOrNull()
    }

    override suspend fun shouldUpdateAccountCache(localAccountAddresses: List<String>): PeraResult<Boolean> {
        val body = ShouldRefreshRequestBody(localAccountAddresses, getLastKnownAccountBlockNumber())
        return requestWithHipoErrorHandler(peraErrorHandler) { blockPollingApiService.shouldRefresh(body) }.map {
            it.shouldRefresh ?: false
        }
    }
}

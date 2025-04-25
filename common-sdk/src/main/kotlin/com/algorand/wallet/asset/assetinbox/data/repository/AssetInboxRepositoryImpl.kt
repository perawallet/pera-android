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

package com.algorand.wallet.asset.assetinbox.data.repository

import com.algorand.wallet.asset.assetinbox.data.mapper.AssetInboxRequestMapper
import com.algorand.wallet.asset.assetinbox.data.service.AssetInboxApiService
import com.algorand.wallet.asset.assetinbox.domain.model.AssetInboxRequest
import com.algorand.wallet.asset.assetinbox.domain.repository.AssetInboxRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.InMemoryLocalCache
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import com.algorand.wallet.foundation.network.utils.requestWithHipoErrorHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AssetInboxRepositoryImpl(
    private val assetInboxApiService: AssetInboxApiService,
    private val retrofitErrorHandler: PeraRetrofitErrorHandler,
    private val assetInboxRequestMapper: AssetInboxRequestMapper,
    private val inMemoryLocalCache: InMemoryLocalCache<String, AssetInboxRequest>
) : AssetInboxRepository {

    override suspend fun getRequests(addresses: List<String>): PeraResult<List<AssetInboxRequest>> {
        return requestWithHipoErrorHandler(retrofitErrorHandler) {
            assetInboxApiService.getAssetInboxAllAccountsRequests(addresses.joinToString(","))
        }.map { response ->
            assetInboxRequestMapper(response)
        }
    }

    override suspend fun cacheRequests(requests: List<AssetInboxRequest>) {
        val cacheData = requests.map { it.address to it }
        inMemoryLocalCache.putAll(cacheData)
    }

    override fun getRequestCountFlow(): Flow<Int> {
        return inMemoryLocalCache.getCacheFlow().map { cacheMap ->
            cacheMap.values.sumOf { request ->
                request.requestCount
            }
        }
    }

    override suspend fun clearCache() {
        inMemoryLocalCache.clear()
    }

    override suspend fun getRequest(address: String): AssetInboxRequest? = inMemoryLocalCache[address]
}

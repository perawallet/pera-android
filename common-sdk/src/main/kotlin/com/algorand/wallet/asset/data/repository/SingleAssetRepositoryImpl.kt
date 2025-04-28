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

package com.algorand.wallet.asset.data.repository

import com.algorand.wallet.asset.data.mapper.model.AssetMapper
import com.algorand.wallet.asset.data.service.AssetDetailApiService
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.repository.SingleAssetRepository
import com.algorand.wallet.foundation.cache.CacheResult
import com.algorand.wallet.foundation.cache.SingleInMemoryLocalCache
import com.algorand.wallet.foundation.network.utils.request
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal class SingleAssetRepositoryImpl @Inject constructor(
    private val assetDetailApi: AssetDetailApiService,
    private val assetCache: SingleInMemoryLocalCache<Asset>,
    private val assetMapper: AssetMapper
) : SingleAssetRepository {

    override suspend fun cacheAssetDetail(assetId: Long) {
        request { assetDetailApi.getAssetDetail(assetId) }.use(
            onSuccess = {
                val asset = assetMapper(it)
                asset?.let { assetCache.put(CacheResult.Success.create(asset)) }
            },
            onFailed = { exception, code ->
                assetCache.put(CacheResult.Error.create(exception, code = code))
            }
        )
    }

    override fun getAssetDetailFlow(): Flow<Asset> {
        return assetCache.cacheFlow.mapNotNull {
            it?.getDataOrNull()
        }
    }

    override suspend fun clearCache() {
        assetCache.clear()
    }
}

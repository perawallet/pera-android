/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.asset.detail.component.assetabout.data.repository

import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.AssetMapper
import com.algorand.android.module.asset.detail.component.asset.data.service.AssetDetailApi
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import com.algorand.android.module.asset.detail.component.assetabout.domain.repository.AssetAboutRepository
import com.algorand.android.caching.CacheResult
import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import com.algorand.android.network_utils.requestWithHipoErrorHandler
import kotlinx.coroutines.flow.StateFlow

internal class AssetAboutRepositoryImpl(
    private val assetDetailApi: AssetDetailApi,
    private val hipoApiErrorHandler: RetrofitErrorHandler,
    private val assetDetailLocalCache: SingleInMemoryLocalCache<Asset>,
    private val assetMapper: AssetMapper
) : AssetAboutRepository {

    override suspend fun cacheAssetDetailToAsaProfile(assetId: Long) {
        requestWithHipoErrorHandler(hipoApiErrorHandler) {
            assetDetailApi.getAssetDetail(assetId)
        }.use(
            onSuccess = { response ->
                val assetDetail = assetMapper(response)
                if (assetDetail == null) {
                    assetDetailLocalCache.put(CacheResult.Error.create(Exception("AssetDetail is null")))
                } else {
                    assetDetailLocalCache.put(CacheResult.Success.create(assetDetail))
                }
            },
            onFailed = { exception, _ ->
                assetDetailLocalCache.put(CacheResult.Error.create(exception))
            }
        )
    }

    override fun getAssetFlowFromAsaProfileCache(): StateFlow<CacheResult<Asset>?> {
        return assetDetailLocalCache.cacheFlow
    }

    override fun clearAsaProfileCache() {
        assetDetailLocalCache.clear()
    }
}

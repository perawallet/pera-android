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

package com.algorand.android.repository

import com.algorand.android.cache.SimpleAssetLocalCache
import com.algorand.android.exceptions.RetrofitErrorHandler
import com.algorand.android.models.AssetDetail
import com.algorand.android.models.AssetDetailResponse
import com.algorand.android.models.AssetSupportRequest
import com.algorand.android.models.NodeAssetDetailResponse
import com.algorand.android.models.Result
import com.algorand.android.network.AlgodApi
import com.algorand.android.network.MobileAlgorandApi
import com.algorand.android.network.request
import com.algorand.android.network.requestWithHipoErrorHandler
import com.algorand.android.network.safeApiCall
import com.algorand.android.utils.AlgoAssetInformationProvider
import com.algorand.android.utils.CacheResult
import com.algorand.android.utils.toQueryString
import com.algorand.wallet.asset.domain.util.AssetConstants
import javax.inject.Inject

class AssetRepository @Inject constructor(
    private val algodApi: AlgodApi,
    private val mobileAlgorandApi: MobileAlgorandApi,
    private val hipoApiErrorHandler: RetrofitErrorHandler,
    private val simpleAssetLocalCache: SimpleAssetLocalCache,
    private val algoAssetInformationProvider: AlgoAssetInformationProvider
) {
    suspend fun fetchAssetsById(assetIdList: List<Long>, includeDeleted: Boolean? = null) =
        requestWithHipoErrorHandler(hipoApiErrorHandler) {
            mobileAlgorandApi.getAssetsByIds(assetIdList.toQueryString(), includeDeleted)
        }

    suspend fun postAssetSupportRequest(assetSupportRequest: AssetSupportRequest): Result<Unit> {
        return safeApiCall { requestPostAssetSupportRequest(assetSupportRequest) }
    }

    private suspend fun requestPostAssetSupportRequest(assetSupportRequest: AssetSupportRequest) =
        requestWithHipoErrorHandler(hipoApiErrorHandler) {
            mobileAlgorandApi.postAssetSupportRequest(assetSupportRequest)
        }

    suspend fun cacheAllAssets(assetKeyValuePairList: List<Pair<Long, CacheResult<AssetDetail>>>) {
        simpleAssetLocalCache.putAll(assetKeyValuePairList)
    }

    fun getAssetCacheFlow() = simpleAssetLocalCache.cacheMapFlow

    fun getCachedAssetById(assetId: Long): CacheResult<AssetDetail>? {
        return if (assetId == AssetConstants.ALGO_ID) {
            algoAssetInformationProvider.getAlgoAssetInformation()
        } else {
            simpleAssetLocalCache.getOrNull(assetId)
        }
    }

    suspend fun clearAssetCache() {
        simpleAssetLocalCache.clear()
    }

    suspend fun clearAssetCache(assetId: Long) {
        simpleAssetLocalCache.remove(assetId)
    }

    suspend fun getAssetDetailFromNode(assetId: Long): Result<NodeAssetDetailResponse> {
        return request { algodApi.getAssetDetail(assetId) }
    }

    suspend fun getAssetDetailFromIndexer(assetId: Long): Result<AssetDetailResponse> {
        return requestWithHipoErrorHandler(hipoApiErrorHandler) {
            mobileAlgorandApi.getAssetDetail(assetId)
        }
    }
}

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

package com.algorand.common.asset.domain.repository

import com.algorand.common.asset.domain.model.Asset
import com.algorand.common.asset.domain.model.AssetDetail
import com.algorand.common.asset.domain.model.CollectibleDetail
import com.algorand.common.foundation.PeraResult

internal interface AssetRepository {

    suspend fun fetchAsset(assetId: Long): PeraResult<Asset>

    suspend fun fetchAssets(assetIds: List<Long>): PeraResult<List<Asset>>

    suspend fun fetchAssetDetailFromNode(assetId: Long): PeraResult<AssetDetail>

    suspend fun fetchAndCacheAssets(assetIds: List<Long>, includeDeleted: Boolean): PeraResult<Unit>

    suspend fun getAssetDetail(assetId: Long): AssetDetail?

    suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail?

    suspend fun getAsset(assetId: Long): Asset?

    suspend fun clearCache()

    suspend fun getCollectiblesDetail(collectibleIds: List<Long>): List<CollectibleDetail>

    suspend fun fetchCollectibleDetail(collectibleAssetId: Long): PeraResult<CollectibleDetail>
}

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

package com.algorand.wallet.asset.domain.repository

import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.AssetDetail
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import com.algorand.wallet.asset.lite.domain.model.AssetLiteInformation
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

internal interface AssetRepository {

    suspend fun fetchAsset(assetId: Long, deviceId: String?): PeraResult<Asset>

    suspend fun fetchAssets(assetIds: List<Long>, deviceId: String?): PeraResult<List<Asset>>

    suspend fun fetchAssetDetailFromNode(assetId: Long): PeraResult<AssetDetail>

    suspend fun fetchAndCacheAssets(assetIds: List<Long>, deviceId: String?, includeDeleted: Boolean): PeraResult<Unit>

    suspend fun getAssetDetail(assetId: Long): AssetDetail?

    suspend fun getAssetsDetail(assetIds: List<Long>): List<AssetDetail>

    suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail?

    suspend fun getAsset(assetId: Long): Asset?

    suspend fun isCollectibleExist(collectibleId: Long): Boolean

    suspend fun clearCache()

    suspend fun getCollectiblesDetail(collectibleIds: List<Long>): List<CollectibleDetail>

    suspend fun fetchCollectibleDetail(collectibleAssetId: Long, deviceId: String?): PeraResult<CollectibleDetail>

    suspend fun getCachedAssetIds(): List<Long>

    fun getAssetsLiteInformationFlow(assetIds: List<Long>): Flow<Map<Long, AssetLiteInformation?>>

    suspend fun getAssetLiteInformation(assetId: Long): AssetLiteInformation?

    suspend fun getAssetCreatorAddress(assetId: Long): String?

    suspend fun cacheAlgoAssetDetail(usdValue: BigDecimal?)

    suspend fun getRecentlyAddedCollectibleUrls(count: Int): List<String>

    suspend fun setFavoriteStatus(assetId: Long, deviceId: String, isFavorite: Boolean): PeraResult<Unit>

    suspend fun setPriceAlertStatus(assetId: Long, deviceId: String, enabled: Boolean): PeraResult<Unit>

    suspend fun getFavoriteStatuses(assetIds: List<Long>): Map<Long, Boolean?>
}

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

package com.algorand.common.asset.data.repository

import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.asset.domain.model.Asset
import com.algorand.common.asset.domain.model.AssetDetail
import com.algorand.common.asset.domain.model.CollectibleDetail

internal interface AssetDetailCacheHelper {

    suspend fun cacheAssetDetails(assetDetails: List<AssetResponse>)

    suspend fun getAssetDetail(assetId: Long): AssetDetail?

    suspend fun getAsset(assetId: Long): Asset?

    suspend fun getCollectibleDetail(collectibleId: Long): CollectibleDetail?

    suspend fun getCollectibleDetails(collectibleIds: List<Long>): List<CollectibleDetail>
}

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

package com.algorand.wallet.asset.domain.usecase

import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.AssetCacheStatus
import com.algorand.wallet.asset.domain.model.AssetDetail
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.flow.Flow

fun interface ClearAssetCache {
    suspend operator fun invoke()
}

fun interface FetchAndCacheAssets {
    suspend operator fun invoke(assetIds: List<Long>, includeDeleted: Boolean): PeraResult<Unit>
}

fun interface FetchAsset {
    suspend operator fun invoke(assetId: Long): PeraResult<Asset>
}

fun interface FetchAssetDetailFromNode {
    suspend operator fun invoke(assetId: Long): PeraResult<AssetDetail>
}

fun interface FetchAssets {
    suspend operator fun invoke(assetIds: List<Long>): PeraResult<List<Asset>>
}

fun interface GetAsset {
    suspend operator fun invoke(assetId: Long): Asset?
}

fun interface GetAssetDetail {
    suspend operator fun invoke(assetId: Long): AssetDetail?
}

fun interface GetAssetDetailCacheStatusFlow {
    operator fun invoke(): Flow<AssetCacheStatus>
}

fun interface GetCollectibleDetail {
    suspend operator fun invoke(collectibleId: Long): CollectibleDetail?
}

fun interface GetCollectiblesDetail {
    suspend operator fun invoke(collectibleIds: List<Long>): List<CollectibleDetail>
}

fun interface InitializeAssets {
    suspend operator fun invoke(assetIds: List<Long>)
}

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

package com.algorand.common.asset.di

import com.algorand.common.asset.domain.repository.AssetRepository
import com.algorand.common.asset.domain.usecase.ClearAssetCache
import com.algorand.common.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.common.asset.domain.usecase.FetchAsset
import com.algorand.common.asset.domain.usecase.FetchAssetDetailFromNode
import com.algorand.common.asset.domain.usecase.GetAsset
import com.algorand.common.asset.domain.usecase.GetAssetDetail
import com.algorand.common.asset.domain.usecase.GetCollectibleDetail
import com.algorand.common.asset.domain.usecase.GetCollectiblesDetail
import com.algorand.common.asset.domain.usecase.InitializeAssets
import com.algorand.common.asset.domain.usecase.InitializeAssetsUseCase
import org.koin.dsl.module

internal val assetDetailUseCaseModule = module {
    factory<ClearAssetCache> {
        ClearAssetCache {
            get<AssetRepository>().clearCache()
        }
    }

    factory<FetchAndCacheAssets> {
        FetchAndCacheAssets { assetIds, includeDeleted ->
            get<AssetRepository>().fetchAndCacheAssets(assetIds, includeDeleted)
        }
    }

    factory<FetchAsset> {
        FetchAsset { assetId ->
            get<AssetRepository>().fetchAsset(assetId)
        }
    }

    factory<FetchAssetDetailFromNode> {
        FetchAssetDetailFromNode { assetId ->
            get<AssetRepository>().fetchAssetDetailFromNode(assetId)
        }
    }

    factory<GetAsset> {
        GetAsset { assetId ->
            get<AssetRepository>().getAsset(assetId)
        }
    }

    factory<GetAssetDetail> {
        GetAssetDetail { assetId ->
            get<AssetRepository>().getAssetDetail(assetId)
        }
    }

    factory<GetCollectibleDetail> {
        GetCollectibleDetail { collectibleId ->
            get<AssetRepository>().getCollectibleDetail(collectibleId)
        }
    }

    factory<GetCollectiblesDetail> {
        GetCollectiblesDetail { collectibleIds ->
            get<AssetRepository>().getCollectiblesDetail(collectibleIds)
        }
    }

    factory<InitializeAssets> { InitializeAssetsUseCase(get()) }
}

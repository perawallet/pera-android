/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.di

import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.asset.domain.usecase.ClearAssetCache
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheMissingAssets
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheMissingAssetsUseCase
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import com.algorand.wallet.asset.domain.usecase.FetchAssetDetailFromNode
import com.algorand.wallet.asset.domain.usecase.FetchAssets
import com.algorand.wallet.asset.domain.usecase.FetchCollectibleDetail
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.asset.domain.usecase.GetAssetDetail
import com.algorand.wallet.asset.domain.usecase.GetCollectibleDetail
import com.algorand.wallet.asset.domain.usecase.GetCollectiblesDetail
import com.algorand.wallet.asset.domain.usecase.InitializeAssets
import com.algorand.wallet.asset.domain.usecase.InitializeAssetsUseCase
import com.algorand.wallet.asset.domain.usecase.IsCollectibleExist
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AssetDetailUseCaseModule {

    @Provides
    fun provideClearAssetCache(repository: AssetRepository): ClearAssetCache {
        return ClearAssetCache(repository::clearCache)
    }

    @Provides
    fun provideFetchAndCacheAssets(repository: AssetRepository): FetchAndCacheAssets {
        return FetchAndCacheAssets(repository::fetchAndCacheAssets)
    }

    @Provides
    fun provideFetchAsset(repository: AssetRepository): FetchAsset {
        return FetchAsset(repository::fetchAsset)
    }

    @Provides
    fun provideFetchAssets(repository: AssetRepository): FetchAssets {
        return FetchAssets(repository::fetchAssets)
    }

    @Provides
    fun provideFetchAssetDetailFromNode(repository: AssetRepository): FetchAssetDetailFromNode {
        return FetchAssetDetailFromNode(repository::fetchAssetDetailFromNode)
    }

    @Provides
    fun provideGetAsset(repository: AssetRepository): GetAsset {
        return GetAsset(repository::getAsset)
    }

    @Provides
    fun provideGetAssetDetail(repository: AssetRepository): GetAssetDetail {
        return GetAssetDetail(repository::getAssetDetail)
    }

    @Provides
    fun provideGetCollectibleDetail(repository: AssetRepository): GetCollectibleDetail {
        return GetCollectibleDetail(repository::getCollectibleDetail)
    }

    @Provides
    fun provideFetchCollectibleDetail(repository: AssetRepository): FetchCollectibleDetail {
        return FetchCollectibleDetail(repository::fetchCollectibleDetail)
    }

    @Provides
    fun provideGetCollectiblesDetail(repository: AssetRepository): GetCollectiblesDetail {
        return GetCollectiblesDetail(repository::getCollectiblesDetail)
    }

    @Provides
    fun provideInitializeAssets(useCase: InitializeAssetsUseCase): InitializeAssets = useCase

    @Provides
    fun provideFetchAndCacheMissingAssets(
        useCase: FetchAndCacheMissingAssetsUseCase
    ): FetchAndCacheMissingAssets = useCase

    @Provides
    fun provideIsCollectibleExist(
        repository: AssetRepository
    ): IsCollectibleExist {
        return IsCollectibleExist(repository::isCollectibleExist)
    }
}

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

package com.algorand.wallet.asset.di

import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.asset.domain.usecase.CacheAlgoAssetDetail
import com.algorand.wallet.asset.domain.usecase.ClearAssetCache
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssetsUseCase
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheMissingAssets
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheMissingAssetsUseCase
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import com.algorand.wallet.asset.domain.usecase.FetchAssetDetailFromNode
import com.algorand.wallet.asset.domain.usecase.FetchAssetUseCase
import com.algorand.wallet.asset.domain.usecase.FetchAssets
import com.algorand.wallet.asset.domain.usecase.FetchAssetsUseCase
import com.algorand.wallet.asset.domain.usecase.FetchCollectibleDetail
import com.algorand.wallet.asset.domain.usecase.FetchCollectibleDetailUseCase
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.asset.domain.usecase.GetAssetCreatorAddress
import com.algorand.wallet.asset.domain.usecase.GetAssetDetail
import com.algorand.wallet.asset.domain.usecase.GetAssetDetails
import com.algorand.wallet.asset.domain.usecase.GetAssetFavoriteStatuses
import com.algorand.wallet.asset.domain.usecase.GetCollectibleDetail
import com.algorand.wallet.asset.domain.usecase.GetCollectiblesDetail
import com.algorand.wallet.asset.domain.usecase.GetRecentlyAddedCollectibleUrls
import com.algorand.wallet.asset.domain.usecase.GetUsdcAssetId
import com.algorand.wallet.asset.domain.usecase.GetUsdcAssetIdUseCase
import com.algorand.wallet.asset.domain.usecase.IsCollectibleExist
import com.algorand.wallet.asset.domain.usecase.SetAssetFavoriteStatus
import com.algorand.wallet.asset.domain.usecase.SetAssetFavoriteStatusUseCase
import com.algorand.wallet.asset.domain.usecase.SetAssetPriceAlertStatus
import com.algorand.wallet.asset.domain.usecase.SetAssetPriceAlertStatusUseCase
import com.algorand.wallet.asset.lite.domain.usecase.GetAssetLiteInformation
import com.algorand.wallet.asset.lite.domain.usecase.GetAssetsLiteInformationFlow
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
    fun provideFetchAndCacheAssets(useCase: FetchAndCacheAssetsUseCase): FetchAndCacheAssets = useCase

    @Provides
    fun provideFetchAsset(useCase: FetchAssetUseCase): FetchAsset = useCase

    @Provides
    fun provideFetchAssets(useCase: FetchAssetsUseCase): FetchAssets = useCase

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
    fun provideFetchCollectibleDetail(useCase: FetchCollectibleDetailUseCase): FetchCollectibleDetail = useCase

    @Provides
    fun provideGetCollectiblesDetail(repository: AssetRepository): GetCollectiblesDetail {
        return GetCollectiblesDetail(repository::getCollectiblesDetail)
    }

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

    @Provides
    fun provideGetAssetsLiteInformationFlow(
        repository: AssetRepository
    ): GetAssetsLiteInformationFlow {
        return GetAssetsLiteInformationFlow(repository::getAssetsLiteInformationFlow)
    }

    @Provides
    fun provideGetAssetLiteInformation(repository: AssetRepository): GetAssetLiteInformation {
        return GetAssetLiteInformation(repository::getAssetLiteInformation)
    }

    @Provides
    fun provideGetAssetCreatorAddress(repository: AssetRepository): GetAssetCreatorAddress {
        return GetAssetCreatorAddress(repository::getAssetCreatorAddress)
    }

    @Provides
    fun provideGetAssetDetails(repository: AssetRepository): GetAssetDetails {
        return GetAssetDetails(repository::getAssetsDetail)
    }

    @Provides
    fun provideCacheAlgoAssetDetail(repository: AssetRepository): CacheAlgoAssetDetail {
        return CacheAlgoAssetDetail(repository::cacheAlgoAssetDetail)
    }

    @Provides
    fun provideGetRecentlyAddedCollectibleUrls(repository: AssetRepository): GetRecentlyAddedCollectibleUrls {
        return GetRecentlyAddedCollectibleUrls(repository::getRecentlyAddedCollectibleUrls)
    }

    @Provides
    fun provideGetUsdcAssetId(useCase: GetUsdcAssetIdUseCase): GetUsdcAssetId = useCase

    @Provides
    fun provideSetAssetPriceAlertStatus(useCase: SetAssetPriceAlertStatusUseCase): SetAssetPriceAlertStatus = useCase

    @Provides
    fun provideSetAssetFavoriteStatus(useCase: SetAssetFavoriteStatusUseCase): SetAssetFavoriteStatus = useCase

    @Provides
    fun provideGetAssetFavoriteStatuses(repository: AssetRepository): GetAssetFavoriteStatuses {
        return GetAssetFavoriteStatuses(repository::getFavoriteStatuses)
    }
}

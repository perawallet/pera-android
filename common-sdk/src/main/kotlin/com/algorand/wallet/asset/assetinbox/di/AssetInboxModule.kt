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

package com.algorand.wallet.asset.assetinbox.di

import com.algorand.wallet.asset.assetinbox.data.mapper.AssetInboxRequestMapper
import com.algorand.wallet.asset.assetinbox.data.mapper.AssetInboxRequestMapperImpl
import com.algorand.wallet.asset.assetinbox.data.repository.AssetInboxRepositoryImpl
import com.algorand.wallet.asset.assetinbox.data.service.AssetInboxApiService
import com.algorand.wallet.asset.assetinbox.domain.AssetInboxCacheManager
import com.algorand.wallet.asset.assetinbox.domain.AssetInboxCacheManagerImpl
import com.algorand.wallet.asset.assetinbox.domain.repository.AssetInboxRepository
import com.algorand.wallet.asset.assetinbox.domain.usecase.CacheAssetInboxRequests
import com.algorand.wallet.asset.assetinbox.domain.usecase.ClearAssetInboxCache
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequest
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequestCountFlow
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequests
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxValidAddresses
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxValidAddressesUseCase
import com.algorand.wallet.foundation.cache.InMemoryLocalCache
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AssetInboxModule {

    @Provides
    fun provideAssetInboxRequestMapper(impl: AssetInboxRequestMapperImpl): AssetInboxRequestMapper = impl

    @Provides
    @Singleton
    fun provideAssetInboxCacheManager(impl: AssetInboxCacheManagerImpl): AssetInboxCacheManager = impl

    @Provides
    @Singleton
    fun provideAssetInboxAllAccountsApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetInboxApiService {
        return retrofit.create(AssetInboxApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAssetInboxRepository(
        assetInboxApiService: AssetInboxApiService,
        retrofitErrorHandler: PeraRetrofitErrorHandler,
        assetInboxRequestMapper: AssetInboxRequestMapper,
    ): AssetInboxRepository {
        return AssetInboxRepositoryImpl(
            assetInboxApiService,
            retrofitErrorHandler,
            assetInboxRequestMapper,
            InMemoryLocalCache()
        )
    }

    @Provides
    fun provideGetAssetInboxRequests(repository: AssetInboxRepository): GetAssetInboxRequests {
        return GetAssetInboxRequests(repository::getRequests)
    }

    @Provides
    fun provideCacheAssetInboxRequests(repository: AssetInboxRepository): CacheAssetInboxRequests {
        return CacheAssetInboxRequests(repository::cacheRequests)
    }

    @Provides
    fun provideClearAssetInboxCache(repository: AssetInboxRepository): ClearAssetInboxCache {
        return ClearAssetInboxCache(repository::clearCache)
    }

    @Provides
    fun provideGetAssetInboxRequestCountFlow(repository: AssetInboxRepository): GetAssetInboxRequestCountFlow {
        return GetAssetInboxRequestCountFlow(repository::getRequestCountFlow)
    }

    @Provides
    fun provideGetAssetInboxRequest(repository: AssetInboxRepository): GetAssetInboxRequest {
        return GetAssetInboxRequest(repository::getRequest)
    }

    @Provides
    fun provideGetAssetInboxValidAddresses(
        useCase: GetAssetInboxValidAddressesUseCase
    ): GetAssetInboxValidAddresses = useCase
}

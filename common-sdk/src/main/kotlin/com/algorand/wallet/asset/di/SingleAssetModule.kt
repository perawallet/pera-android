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

import com.algorand.wallet.asset.data.mapper.model.AssetMapper
import com.algorand.wallet.asset.data.repository.SingleAssetRepositoryImpl
import com.algorand.wallet.asset.data.service.AssetDetailApiService
import com.algorand.wallet.asset.domain.repository.SingleAssetRepository
import com.algorand.wallet.asset.domain.usecase.CacheSingleAssetDetail
import com.algorand.wallet.asset.domain.usecase.ClearSingleAssetCache
import com.algorand.wallet.asset.domain.usecase.GetSingleAssetDetailFlow
import com.algorand.wallet.foundation.cache.SingleInMemoryLocalCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SingleAssetModule {

    @Provides
    @Singleton
    fun provideSingleAssetRepository(
        assetDetailApiService: AssetDetailApiService,
        assetMapper: AssetMapper
    ): SingleAssetRepository {
        return SingleAssetRepositoryImpl(
            assetDetailApiService,
            SingleInMemoryLocalCache(),
            assetMapper
        )
    }

    @Provides
    fun provideCacheSingleAssetDetail(
        repository: SingleAssetRepository
    ): CacheSingleAssetDetail {
        return CacheSingleAssetDetail(repository::cacheAssetDetail)
    }

    @Provides
    fun provideGetSingleAssetDetailFlow(
        repository: SingleAssetRepository
    ): GetSingleAssetDetailFlow {
        return GetSingleAssetDetailFlow(repository::getAssetDetailFlow)
    }

    @Provides
    fun provideClearSingleAssetCache(
        repository: SingleAssetRepository
    ): ClearSingleAssetCache {
        return ClearSingleAssetCache(repository::clearCache)
    }
}

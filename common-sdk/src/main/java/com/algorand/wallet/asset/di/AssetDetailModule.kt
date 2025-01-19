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

package com.algorand.wallet.asset.di

import com.algorand.wallet.asset.data.repository.AssetDetailCacheHelper
import com.algorand.wallet.asset.data.repository.AssetDetailCacheHelperImpl
import com.algorand.wallet.asset.data.repository.AssetRepositoryImpl
import com.algorand.wallet.asset.data.service.AssetDetailApiService
import com.algorand.wallet.asset.data.service.AssetDetailNodeApiService
import com.algorand.wallet.asset.domain.manager.AssetDetailCacheManager
import com.algorand.wallet.asset.domain.manager.AssetDetailCacheManagerImpl
import com.algorand.wallet.asset.domain.repository.AssetRepository
import com.algorand.wallet.asset.domain.usecase.GetAssetDetailCacheStatusFlow
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AssetDetailModule {

    @Provides
    @Singleton
    fun provideAssetDetailApiServiceImpl(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): AssetDetailApiService {
        return retrofit.create(AssetDetailApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAssetDetailNodeApiServiceImpl(
        @Named("algodRetrofitInterface") retrofit: Retrofit
    ): AssetDetailNodeApiService {
        return retrofit.create(AssetDetailNodeApiService::class.java)
    }

    @Provides
    fun provideAssetDetailCacheHelper(impl: AssetDetailCacheHelperImpl): AssetDetailCacheHelper = impl

    @Provides
    @Singleton
    fun provideAssetRepository(impl: AssetRepositoryImpl): AssetRepository = impl

    @Provides
    @Singleton
    fun provideAssetDetailCacheManager(impl: AssetDetailCacheManagerImpl): AssetDetailCacheManager = impl

    @Provides
    fun provideGetAssetDetailCacheStatusFlow(manager: AssetDetailCacheManager): GetAssetDetailCacheStatusFlow {
        return GetAssetDetailCacheStatusFlow(manager::cacheStatusFlow)
    }
}
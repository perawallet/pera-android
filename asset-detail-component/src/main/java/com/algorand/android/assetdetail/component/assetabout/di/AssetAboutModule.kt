/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.assetdetail.component.assetabout.di

import com.algorand.android.assetdetail.component.asset.data.mapper.model.AssetMapper
import com.algorand.android.assetdetail.component.asset.data.service.AssetDetailApi
import com.algorand.android.assetdetail.component.assetabout.data.repository.AssetAboutRepositoryImpl
import com.algorand.android.assetdetail.component.assetabout.domain.repository.AssetAboutRepository
import com.algorand.android.assetdetail.component.assetabout.domain.usecase.CacheAssetDetailToAsaProfile
import com.algorand.android.assetdetail.component.assetabout.domain.usecase.ClearAsaProfileCache
import com.algorand.android.assetdetail.component.assetabout.domain.usecase.GetAssetFlowFromAsaProfileCache
import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetAboutModule {

    @Provides
    @Singleton
    fun provideAssetAboutRepository(
        assetDetailApi: AssetDetailApi,
        hipoApiErrorHandler: RetrofitErrorHandler,
        assetMapper: AssetMapper
    ): AssetAboutRepository {
        return AssetAboutRepositoryImpl(
            assetDetailApi,
            hipoApiErrorHandler,
            SingleInMemoryLocalCache(),
            assetMapper
        )
    }

    @Provides
    @Singleton
    fun provideCacheAssetDetailToAsaProfile(
        repository: AssetAboutRepository
    ): CacheAssetDetailToAsaProfile = CacheAssetDetailToAsaProfile(repository::cacheAssetDetailToAsaProfile)

    @Provides
    @Singleton
    fun provideGetAssetDetailFlowFromAsaProfileCache(
        repository: AssetAboutRepository
    ): GetAssetFlowFromAsaProfileCache {
        return GetAssetFlowFromAsaProfileCache(repository::getAssetFlowFromAsaProfileCache)
    }

    @Provides
    @Singleton
    fun provideClearAsaProfileCache(
        repository: AssetAboutRepository
    ): ClearAsaProfileCache = ClearAsaProfileCache(repository::clearAsaProfileCache)
}
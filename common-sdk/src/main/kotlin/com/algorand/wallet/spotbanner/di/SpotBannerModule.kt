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

package com.algorand.wallet.spotbanner.di

import com.algorand.wallet.foundation.cache.InMemoryCacheProvider
import com.algorand.wallet.spotbanner.data.cache.DefaultSpotBannerInMemoryCache
import com.algorand.wallet.spotbanner.data.cache.SpotBannerInMemoryCache
import com.algorand.wallet.spotbanner.data.mapper.DefaultSpotBannerCacheDataMapper
import com.algorand.wallet.spotbanner.data.mapper.DefaultSpotBannerMapper
import com.algorand.wallet.spotbanner.data.mapper.SpotBannerCacheDataMapper
import com.algorand.wallet.spotbanner.data.mapper.SpotBannerMapper
import com.algorand.wallet.spotbanner.data.repository.DefaultSpotBannerRepository
import com.algorand.wallet.spotbanner.data.service.SpotBannerApiService
import com.algorand.wallet.spotbanner.domain.repository.SpotBannerRepository
import com.algorand.wallet.spotbanner.domain.usecase.ClearSpotBannerCache
import com.algorand.wallet.spotbanner.domain.usecase.DismissSpotBanner
import com.algorand.wallet.spotbanner.domain.usecase.DismissSpotBannerUseCase
import com.algorand.wallet.spotbanner.domain.usecase.GetSpotBannersFlow
import com.algorand.wallet.spotbanner.domain.usecase.GetSpotBannersFlowUseCase
import com.algorand.wallet.spotbanner.domain.usecase.InitializeSpotBanners
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object SpotBannerModule {

    @Provides
    @Singleton
    fun provideSpotBannerInMemoryCache(inMemoryCacheProvider: InMemoryCacheProvider): SpotBannerInMemoryCache {
        return DefaultSpotBannerInMemoryCache(inMemoryCacheProvider.getFlowInMemoryCache(emptyArray()))
    }

    @Provides
    fun provideSpotBannerMapper(mapper: DefaultSpotBannerMapper): SpotBannerMapper = mapper

    @Provides
    fun provideSpotBannerCacheDataMapper(
        mapper: DefaultSpotBannerCacheDataMapper
    ): SpotBannerCacheDataMapper = mapper

    @Provides
    @Singleton
    fun provideSpotBannerApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): SpotBannerApiService {
        return retrofit.create(SpotBannerApiService::class.java)
    }

    @Provides
    fun provideSpotBannerRepository(repository: DefaultSpotBannerRepository): SpotBannerRepository = repository

    @Provides
    fun provideInitializeSpotBanners(repository: SpotBannerRepository): InitializeSpotBanners {
        return InitializeSpotBanners(repository::cacheBanners)
    }

    @Provides
    fun provideGetSpotBannersFlow(useCase: GetSpotBannersFlowUseCase): GetSpotBannersFlow = useCase

    @Provides
    fun provideDismissSpotBanner(useCase: DismissSpotBannerUseCase): DismissSpotBanner = useCase

    @Provides
    fun provideClearSpotBannerCache(spotBannerRepository: SpotBannerRepository): ClearSpotBannerCache {
        return ClearSpotBannerCache(spotBannerRepository::clearBannerCache)
    }
}

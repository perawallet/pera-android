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

package com.algorand.wallet.banner.di

import com.algorand.wallet.banner.data.cache.DefaultDismissedBannerIdsCache
import com.algorand.wallet.banner.data.cache.DismissedBannerIdsCache
import com.algorand.wallet.banner.data.mapper.BannerMapper
import com.algorand.wallet.banner.data.mapper.DefaultBannerMapper
import com.algorand.wallet.banner.data.model.BannerCache
import com.algorand.wallet.banner.data.repository.DefaultBannerRepository
import com.algorand.wallet.banner.data.service.BannerApiService
import com.algorand.wallet.banner.domain.repository.BannerRepository
import com.algorand.wallet.banner.domain.usecase.ClearBannerCache
import com.algorand.wallet.banner.domain.usecase.ClearDismissedBannerIds
import com.algorand.wallet.banner.domain.usecase.DismissBanner
import com.algorand.wallet.banner.domain.usecase.GetBannerFlow
import com.algorand.wallet.banner.domain.usecase.InitializeBanners
import com.algorand.wallet.banner.domain.usecase.InitializeBannersUseCase
import com.algorand.wallet.foundation.cache.InMemoryCacheProvider
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object BannerModule {

    @Provides
    fun provideBannerMapper(mapper: DefaultBannerMapper): BannerMapper = mapper

    @Singleton
    @Provides
    fun provideDismissedBannerIdsCache(cacheProvider: PersistentCacheProvider): DismissedBannerIdsCache {
        return DefaultDismissedBannerIdsCache(
            persistentCache = cacheProvider.getPersistentCache(
                type = Array<Long>::class.java,
                key = "banner_id_list"
            )
        )
    }

    @Provides
    @Singleton
    fun provideBannerRepository(
        bannerApiService: BannerApiService,
        bannerMapper: BannerMapper,
        dismissedBannerIdsCache: DismissedBannerIdsCache,
        inMemoryCacheProvider: InMemoryCacheProvider
    ): BannerRepository {
        return DefaultBannerRepository(
            bannerApiService,
            bannerMapper,
            dismissedBannerIdsCache,
            inMemoryCacheProvider.getFlowInMemoryCache(BannerCache(null))
        )
    }

    @Provides
    @Singleton
    fun provideBannerApiService(@Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit): BannerApiService {
        return retrofit.create(BannerApiService::class.java)
    }

    @Provides
    fun provideInitializeBanners(useCase: InitializeBannersUseCase): InitializeBanners = useCase

    @Provides
    fun provideGetBannerFlow(repository: BannerRepository): GetBannerFlow = GetBannerFlow(repository::getBannerFlow)

    @Provides
    fun provideDismissBanner(repository: BannerRepository): DismissBanner = DismissBanner(repository::dismissBanner)

    @Provides
    fun provideClearBannerCache(repository: BannerRepository): ClearBannerCache {
        return ClearBannerCache(repository::clearBannerCache)
    }

    @Provides
    fun provideClearDismissedBannerIds(repository: BannerRepository): ClearDismissedBannerIds {
        return ClearDismissedBannerIds(repository::clearDismissedBannerIds)
    }
}

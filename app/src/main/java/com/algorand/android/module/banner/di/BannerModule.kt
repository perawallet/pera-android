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

package com.algorand.android.module.banner.di

import android.content.SharedPreferences
import com.algorand.android.module.banner.data.mapper.BannerMapper
import com.algorand.android.module.banner.data.mapper.BannerMapperImpl
import com.algorand.android.module.banner.data.repository.BannerRepositoryImpl
import com.algorand.android.module.banner.data.service.BannerApi
import com.algorand.android.module.banner.data.storage.DismissedBannerIdsLocalSource
import com.algorand.android.module.banner.domain.repository.BannerRepository
import com.algorand.android.module.banner.domain.usecase.ClearBanners
import com.algorand.android.module.banner.domain.usecase.ClearDismissedBannerIds
import com.algorand.android.module.banner.domain.usecase.DismissBanner
import com.algorand.android.module.banner.domain.usecase.GetBannerFlow
import com.algorand.android.module.banner.domain.usecase.InitializeBanners
import com.algorand.android.module.banner.domain.usecase.InitializeBannersUseCase
import com.algorand.android.module.caching.SingleInMemoryLocalCache
import com.algorand.android.foundation.json.JsonSerializer
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
    @Singleton
    fun provideInitializeBanners(useCase: InitializeBannersUseCase): InitializeBanners = useCase

    @Provides
    @Singleton
    fun provideGetBannerFlow(
        bannerRepository: BannerRepository
    ): GetBannerFlow = GetBannerFlow(bannerRepository::getBannerFlow)

    @Provides
    @Singleton
    fun provideDismissBanner(
        bannerRepository: BannerRepository
    ): DismissBanner = DismissBanner(bannerRepository::dismissBanner)

    @Provides
    @Singleton
    fun provideClearBanners(
        bannerRepository: BannerRepository
    ): ClearBanners = ClearBanners(bannerRepository::clearBanners)

    @Provides
    @Singleton
    fun provideClearDismissedBannerIds(
        bannerRepository: BannerRepository
    ): ClearDismissedBannerIds = ClearDismissedBannerIds(bannerRepository::clearDismissedBannerIds)

    @Provides
    @Singleton
    fun provideBannersApi(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): BannerApi = retrofit.create(BannerApi::class.java)

    @Provides
    @Singleton
    fun provideBannerMapper(mapper: BannerMapperImpl): BannerMapper = mapper

    @Provides
    @Singleton
    fun provideBannerRepository(
        bannerApi: BannerApi,
        bannerMapper: BannerMapper,
        sharedPreferences: SharedPreferences,
        jsonSerializer: JsonSerializer
    ): BannerRepository {
        return BannerRepositoryImpl(
            bannerApi = bannerApi,
            dismissedBannerIdsLocalSource = DismissedBannerIdsLocalSource(sharedPreferences, jsonSerializer),
            bannerCache = SingleInMemoryLocalCache(),
            bannerMapper = bannerMapper
        )
    }
}

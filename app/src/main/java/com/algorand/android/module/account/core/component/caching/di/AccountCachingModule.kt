package com.algorand.android.module.account.core.component.caching.di

import com.algorand.android.module.account.core.component.caching.domain.usecase.*
import com.algorand.android.module.account.core.component.caching.domain.usecase.implementation.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountCachingModule {

    @Provides
    @Singleton
    fun provideCacheAccountDetail(useCase: CacheAccountDetailUseCase): CacheAccountDetail = useCase

    @Provides
    @Singleton
    fun provideFetchAccountInformationAndCacheAssetDetails(
        useCase: FetchAccountInformationAndCacheAssetsUseCase
    ): FetchAccountInformationAndCacheAssets = useCase
}

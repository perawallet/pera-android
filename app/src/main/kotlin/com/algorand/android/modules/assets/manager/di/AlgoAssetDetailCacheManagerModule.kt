package com.algorand.android.modules.assets.manager.di

import com.algorand.android.modules.assets.manager.AlgoAssetDetailCacheManagerImpl
import com.algorand.wallet.asset.manager.AlgoAssetDetailCacheManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AlgoAssetDetailCacheManagerModule {

    @Provides
    fun provideAlgoAssetDetailCacheManager(impl: AlgoAssetDetailCacheManagerImpl): AlgoAssetDetailCacheManager = impl
}

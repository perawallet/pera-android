@file:Suppress("TooManyFunctions")
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

package com.algorand.android.module.appcache.di

import com.algorand.android.module.appcache.InitializeAppCache
import com.algorand.android.module.appcache.InitializeAppCacheImpl
import com.algorand.android.module.appcache.manager.InitializeManagerImpl
import com.algorand.android.module.appcache.manager.InitializeManagers
import com.algorand.android.module.appcache.manager.ParityCacheManager
import com.algorand.android.module.appcache.manager.PushTokenManager
import com.algorand.android.module.appcache.manager.PushTokenManagerImpl
import com.algorand.android.module.appcache.usecase.ClearAppSessionCache
import com.algorand.android.module.appcache.usecase.ClearAppSessionCacheUseCase
import com.algorand.android.module.appcache.usecase.ClearPreviousSessionCache
import com.algorand.android.module.appcache.usecase.ClearPreviousSessionCacheUseCase
import com.algorand.android.module.appcache.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.android.module.appcache.usecase.GetAccountDetailCacheStatusFlowUseCase
import com.algorand.android.module.appcache.usecase.GetAppCacheStatusFlow
import com.algorand.android.module.appcache.usecase.GetAppCacheStatusFlowUseCase
import com.algorand.android.module.appcache.usecase.GetAssetDetailCacheStatusFlow
import com.algorand.android.module.appcache.usecase.GetAssetDetailCacheStatusFlowUseCase
import com.algorand.android.module.appcache.usecase.IsAssetCacheStatusAtLeastEmpty
import com.algorand.android.module.appcache.usecase.IsAssetCacheStatusAtLeastEmptyUseCase
import com.algorand.android.module.appcache.usecase.RefreshAccountCacheManager
import com.algorand.android.module.appcache.usecase.RefreshAccountCacheManagerUseCase
import com.algorand.android.module.appcache.usecase.RefreshSelectedCurrencyDetailCache
import com.algorand.android.module.appcache.usecase.UpdateAccountAndAssetCache
import com.algorand.android.module.appcache.usecase.UpdateAccountAndAssetCacheUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CacheManagerModule {

    @Provides
    @Singleton
    fun provideInitializeManager(impl: InitializeManagerImpl): InitializeManagers = impl

    @Provides
    @Singleton
    fun provideUpdateAccountAndAssetCache(
        useCase: UpdateAccountAndAssetCacheUseCase
    ): UpdateAccountAndAssetCache = useCase

    @Provides
    @Singleton
    fun provideGetAccountDetailCacheStatusFlow(
        useCase: GetAccountDetailCacheStatusFlowUseCase
    ): GetAccountDetailCacheStatusFlow = useCase

    @Provides
    @Singleton
    fun provideInitializeAppCache(impl: InitializeAppCacheImpl): InitializeAppCache = impl

    @Provides
    @Singleton
    fun provideGetAppCacheStatusFlow(useCase: GetAppCacheStatusFlowUseCase): GetAppCacheStatusFlow = useCase

    @Provides
    @Singleton
    fun provideClearPreviousSessionCache(useCase: ClearPreviousSessionCacheUseCase): ClearPreviousSessionCache = useCase

    @Provides
    @Singleton
    fun provideGetAssetDetailCacheStatusFlow(
        useCase: GetAssetDetailCacheStatusFlowUseCase
    ): GetAssetDetailCacheStatusFlow = useCase

    @Provides
    @Singleton
    fun provideRefreshSelectedCurrencyDetailCache(manager: ParityCacheManager): RefreshSelectedCurrencyDetailCache {
        return RefreshSelectedCurrencyDetailCache(manager::refreshSelectedCurrencyDetailCache)
    }

    @Provides
    @Singleton
    fun provideIsAssetCacheStatusAtLeastEmpty(
        useCase: IsAssetCacheStatusAtLeastEmptyUseCase
    ): IsAssetCacheStatusAtLeastEmpty = useCase

    @Provides
    @Singleton
    fun provideRefreshAccountCacheManager(
        useCase: RefreshAccountCacheManagerUseCase
    ): RefreshAccountCacheManager = useCase

    @Provides
    @Singleton
    fun provideClearAppSessionCache(
        useCase: ClearAppSessionCacheUseCase
    ): ClearAppSessionCache = useCase

    @Provides
    @Singleton
    fun providePushTokenManager(impl: PushTokenManagerImpl): PushTokenManager = impl
}

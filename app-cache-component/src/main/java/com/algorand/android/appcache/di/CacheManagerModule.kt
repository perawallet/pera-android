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

package com.algorand.android.appcache.di

import com.algorand.android.appcache.InitializeAppCache
import com.algorand.android.appcache.InitializeAppCacheImpl
import com.algorand.android.appcache.manager.InitializeManagerImpl
import com.algorand.android.appcache.manager.InitializeManagers
import com.algorand.android.appcache.manager.ParityCacheManager
import com.algorand.android.appcache.usecase.ClearAppSessionCache
import com.algorand.android.appcache.usecase.ClearAppSessionCacheUseCase
import com.algorand.android.appcache.usecase.ClearPreviousSessionCache
import com.algorand.android.appcache.usecase.ClearPreviousSessionCacheUseCase
import com.algorand.android.appcache.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.android.appcache.usecase.GetAccountDetailCacheStatusFlowUseCase
import com.algorand.android.appcache.usecase.GetAppCacheStatusFlow
import com.algorand.android.appcache.usecase.GetAppCacheStatusFlowUseCase
import com.algorand.android.appcache.usecase.GetAssetDetailCacheStatusFlow
import com.algorand.android.appcache.usecase.GetAssetDetailCacheStatusFlowUseCase
import com.algorand.android.appcache.usecase.IsAssetCacheStatusAtLeastEmpty
import com.algorand.android.appcache.usecase.IsAssetCacheStatusAtLeastEmptyUseCase
import com.algorand.android.appcache.usecase.RefreshAccountCacheManager
import com.algorand.android.appcache.usecase.RefreshAccountCacheManagerUseCase
import com.algorand.android.appcache.usecase.RefreshSelectedCurrencyDetailCache
import com.algorand.android.appcache.usecase.UpdateAccountAndAssetCache
import com.algorand.android.appcache.usecase.UpdateAccountAndAssetCacheUseCase
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
}

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

package com.algorand.wallet.cache.di

import com.algorand.wallet.cache.LifecycleAwareCacheManager
import com.algorand.wallet.cache.LifecycleAwareCacheManagerImpl
import com.algorand.wallet.cache.domain.usecase.ClearPreviousSessionCache
import com.algorand.wallet.cache.domain.usecase.ClearPreviousSessionCacheUseCase
import com.algorand.wallet.cache.domain.usecase.GetAppCacheStatusFlow
import com.algorand.wallet.cache.domain.usecase.GetAppCacheStatusFlowUseCase
import com.algorand.wallet.cache.domain.usecase.InitializeAppCache
import com.algorand.wallet.cache.domain.usecase.InitializeAppCacheImpl
import com.algorand.wallet.cache.domain.usecase.IsAssetCacheStatusAtLeastEmpty
import com.algorand.wallet.cache.domain.usecase.IsAssetCacheStatusAtLeastEmptyUseCase
import com.algorand.wallet.cache.domain.usecase.UpdateAccountCache
import com.algorand.wallet.cache.domain.usecase.UpdateAccountCacheUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CacheModule {

    @Provides
    fun provideLifecycleAwareCacheManager(impl: LifecycleAwareCacheManagerImpl): LifecycleAwareCacheManager = impl

    @Provides
    fun provideClearPreviousSessionCache(useCase: ClearPreviousSessionCacheUseCase): ClearPreviousSessionCache = useCase

    @Provides
    @Singleton
    fun provideInitializeAppCache(impl: InitializeAppCacheImpl): InitializeAppCache = impl

    @Provides
    @Singleton
    fun provideGetAppCacheStatusFlow(useCase: GetAppCacheStatusFlowUseCase): GetAppCacheStatusFlow = useCase

    @Provides
    fun provideUpdateAccountCache(useCase: UpdateAccountCacheUseCase): UpdateAccountCache = useCase

    @Provides
    fun provideIsAssetCacheStatusAtLeastEmpty(
        useCase: IsAssetCacheStatusAtLeastEmptyUseCase
    ): IsAssetCacheStatusAtLeastEmpty = useCase
}

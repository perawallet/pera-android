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

package com.algorand.wallet.inbox.di

import com.algorand.wallet.foundation.cache.InMemoryCacheProvider
import com.algorand.wallet.inbox.data.cache.DefaultInboxInMemoryCache
import com.algorand.wallet.inbox.data.cache.InboxInMemoryCache
import com.algorand.wallet.inbox.data.repository.InboxApiRepositoryImpl
import com.algorand.wallet.inbox.domain.InboxCacheManager
import com.algorand.wallet.inbox.domain.InboxCacheManagerImpl
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.domain.usecase.CacheInboxMessages
import com.algorand.wallet.inbox.domain.usecase.ClearInboxCache
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessages
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddressesUseCase
import com.algorand.wallet.inbox.domain.usecase.HasInboxItemsForAddress
import com.algorand.wallet.inbox.domain.usecase.HasInboxItemsForAddressUseCase
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchMapper
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchMapperImpl
import com.algorand.wallet.inbox.jointaccount.data.service.InboxApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object InboxModule {

    @Provides
    @Singleton
    fun provideInboxApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): InboxApiService {
        return retrofit.create(InboxApiService::class.java)
    }

    @Provides
    @Singleton
    @Named(InboxApiRepository.INJECTION_NAME)
    fun provideInboxApiRepository(
        repository: InboxApiRepositoryImpl
    ): InboxApiRepository = repository

    @Provides
    @Singleton
    fun provideInboxCacheManager(impl: InboxCacheManagerImpl): InboxCacheManager = impl

    @Provides
    @Singleton
    fun provideInboxInMemoryCache(inMemoryCacheProvider: InMemoryCacheProvider): InboxInMemoryCache {
        return DefaultInboxInMemoryCache(inMemoryCacheProvider)
    }

    @Provides
    fun provideInboxSearchMapper(impl: InboxSearchMapperImpl): InboxSearchMapper = impl

    @Provides
    fun provideCacheInboxMessages(cache: InboxInMemoryCache): CacheInboxMessages {
        return CacheInboxMessages(cache::put)
    }

    @Provides
    fun provideClearInboxCache(cache: InboxInMemoryCache): ClearInboxCache {
        return ClearInboxCache(cache::clear)
    }

    @Provides
    fun provideGetInboxMessagesFlow(cache: InboxInMemoryCache): GetInboxMessagesFlow {
        return GetInboxMessagesFlow(cache::observe)
    }

    @Provides
    fun provideGetInboxMessages(cache: InboxInMemoryCache): GetInboxMessages {
        return GetInboxMessages(cache::get)
    }

    @Provides
    fun provideGetInboxValidAddresses(
        useCase: GetInboxValidAddressesUseCase
    ): GetInboxValidAddresses = useCase

    @Provides
    fun provideHasInboxItemsForAddress(
        useCase: HasInboxItemsForAddressUseCase
    ): HasInboxItemsForAddress = useCase

    @Provides
    fun provideRefreshInboxCache(
        inboxCacheManager: InboxCacheManager
    ): RefreshInboxCache = RefreshInboxCache(inboxCacheManager::refreshCache)
}

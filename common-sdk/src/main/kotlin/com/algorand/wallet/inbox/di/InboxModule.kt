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
import com.algorand.wallet.foundation.cache.InMemoryCachedObject
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import com.algorand.wallet.inbox.data.repository.DefaultInboxRepository
import com.algorand.wallet.inbox.data.repository.InboxApiRepositoryImpl
import com.algorand.wallet.inbox.domain.InboxCacheManager
import com.algorand.wallet.inbox.domain.InboxCacheManagerImpl
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.domain.repository.InboxRepository
import com.algorand.wallet.inbox.domain.usecase.CacheInboxMessages
import com.algorand.wallet.inbox.domain.usecase.ClearInboxCache
import com.algorand.wallet.inbox.domain.usecase.GetInboxLastOpenedTime
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessages
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddressesUseCase
import com.algorand.wallet.inbox.domain.usecase.GetJointAccountInboxCountFlow
import com.algorand.wallet.inbox.domain.usecase.GetJointAccountInboxCountFlowUseCase
import com.algorand.wallet.inbox.domain.usecase.HasInboxItemsForAddress
import com.algorand.wallet.inbox.domain.usecase.HasInboxItemsForAddressUseCase
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.inbox.domain.usecase.SetInboxLastOpenedTime
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchMapper
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchMapperImpl
import com.algorand.wallet.inbox.jointaccount.data.service.InboxApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object InboxModule {

    private const val INBOX_CACHE_NAME = "inboxCache"
    private const val INBOX_CACHE_FLOW_NAME = "inboxCacheFlow"
    private const val INBOX_LAST_OPENED_TIME_KEY = "inbox_last_opened_time"

    @Provides
    @Singleton
    fun provideInboxApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): InboxApiService {
        return retrofit.create(InboxApiService::class.java)
    }

    @Provides
    fun provideInboxApiRepository(
        repository: InboxApiRepositoryImpl
    ): InboxApiRepository = repository

    @Provides
    @Singleton
    fun provideInboxCacheManager(impl: InboxCacheManagerImpl): InboxCacheManager = impl

    @Provides
    @Singleton
    @Named(INBOX_CACHE_NAME)
    fun provideInboxCache(
        inMemoryCacheProvider: InMemoryCacheProvider
    ): InMemoryCachedObject<InboxMessages> = inMemoryCacheProvider.getInMemoryCache()

    @Provides
    @Singleton
    @Named(INBOX_CACHE_FLOW_NAME)
    fun provideInboxCacheFlow(): MutableStateFlow<InboxMessages?> = MutableStateFlow(null)

    @Provides
    fun provideInboxSearchMapper(impl: InboxSearchMapperImpl): InboxSearchMapper = impl

    @Provides
    @Singleton
    fun provideInboxRepository(
        @Named(INBOX_CACHE_NAME) inboxCache: InMemoryCachedObject<InboxMessages>,
        @Named(INBOX_CACHE_FLOW_NAME) inboxCacheFlow: MutableStateFlow<InboxMessages?>,
        persistentCacheProvider: PersistentCacheProvider
    ): InboxRepository = DefaultInboxRepository(
        inboxCache = inboxCache,
        inboxCacheFlow = inboxCacheFlow,
        lastOpenedTimeCache = persistentCacheProvider.getPersistentCache(
            String::class.java,
            INBOX_LAST_OPENED_TIME_KEY
        )
    )

    @Provides
    fun provideCacheInboxMessages(
        @Named(INBOX_CACHE_NAME) cache: InMemoryCachedObject<InboxMessages>,
        @Named(INBOX_CACHE_FLOW_NAME) cacheFlow: MutableStateFlow<InboxMessages?>
    ): CacheInboxMessages {
        return CacheInboxMessages { inboxMessages ->
            cache.put(inboxMessages)
            cacheFlow.value = inboxMessages
        }
    }

    @Provides
    fun provideClearInboxCache(
        @Named(INBOX_CACHE_NAME) cache: InMemoryCachedObject<InboxMessages>,
        @Named(INBOX_CACHE_FLOW_NAME) cacheFlow: MutableStateFlow<InboxMessages?>
    ): ClearInboxCache {
        return ClearInboxCache {
            cache.clear()
            cacheFlow.value = null
        }
    }

    @Provides
    fun provideGetInboxMessagesFlow(
        repository: InboxRepository
    ): GetInboxMessagesFlow = GetInboxMessagesFlow(repository::getInboxMessagesFlow)

    @Provides
    fun provideGetInboxMessages(
        repository: InboxRepository
    ): GetInboxMessages = GetInboxMessages(repository::getInboxMessages)

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

    @Provides
    fun provideGetJointAccountInboxCountFlow(
        useCase: GetJointAccountInboxCountFlowUseCase
    ): GetJointAccountInboxCountFlow = useCase

    @Provides
    fun provideSetInboxLastOpenedTime(
        repository: InboxRepository
    ): SetInboxLastOpenedTime = SetInboxLastOpenedTime(repository::setLastOpenedTime)

    @Provides
    fun provideGetInboxLastOpenedTime(
        repository: InboxRepository
    ): GetInboxLastOpenedTime = GetInboxLastOpenedTime(repository::getLastOpenedTime)
}

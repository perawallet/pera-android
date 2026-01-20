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

import com.algorand.wallet.inbox.data.repository.InboxRepositoryImpl
import com.algorand.wallet.inbox.domain.InboxCacheManager
import com.algorand.wallet.inbox.domain.InboxCacheManagerImpl
import com.algorand.wallet.inbox.domain.repository.InboxRepository
import com.algorand.wallet.inbox.domain.usecase.CacheInboxMessages
import com.algorand.wallet.inbox.domain.usecase.ClearInboxCache
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessages
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddressesUseCase
import com.algorand.wallet.inbox.domain.usecase.HasInboxItemsForAddress
import com.algorand.wallet.inbox.domain.usecase.HasInboxItemsForAddressUseCase
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object InboxModule {

    @Provides
    @Singleton
    fun provideInboxCacheManager(impl: InboxCacheManagerImpl): InboxCacheManager = impl

    @Provides
    @Singleton
    fun provideInboxRepository(): InboxRepository {
        return InboxRepositoryImpl()
    }

    @Provides
    fun provideCacheInboxMessages(repository: InboxRepository): CacheInboxMessages {
        return CacheInboxMessages(repository::cacheInboxMessages)
    }

    @Provides
    fun provideClearInboxCache(repository: InboxRepository): ClearInboxCache {
        return ClearInboxCache(repository::clearCache)
    }

    @Provides
    fun provideGetInboxMessagesFlow(repository: InboxRepository): GetInboxMessagesFlow {
        return GetInboxMessagesFlow(repository::getInboxMessagesFlow)
    }

    @Provides
    fun provideGetInboxMessages(repository: InboxRepository): GetInboxMessages {
        return GetInboxMessages(repository::getInboxMessages)
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
    ): RefreshInboxCache = RefreshInboxCache { inboxCacheManager.refreshCache() }
}

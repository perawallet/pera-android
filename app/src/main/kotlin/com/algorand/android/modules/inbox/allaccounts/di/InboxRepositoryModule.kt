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

package com.algorand.android.modules.inbox.allaccounts.di

import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxPreviewMapper
import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxPreviewMapperImpl
import com.algorand.android.modules.inbox.data.local.InboxLastOpenedTimeLocalSource
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object InboxRepositoryModule {

    private const val INBOX_LAST_OPENED_TIME_KEY = "inbox_last_opened_time"

    @Provides
    fun provideInboxPreviewMapper(
        inboxPreviewMapperImpl: InboxPreviewMapperImpl
    ): InboxPreviewMapper = inboxPreviewMapperImpl

    @Provides
    fun provideInboxLastOpenedTimeLocalSource(
        persistentCacheProvider: PersistentCacheProvider
    ): InboxLastOpenedTimeLocalSource {
        return InboxLastOpenedTimeLocalSource(
            persistentCacheProvider.getPersistentCache(String::class.java, INBOX_LAST_OPENED_TIME_KEY)
        )
    }
}

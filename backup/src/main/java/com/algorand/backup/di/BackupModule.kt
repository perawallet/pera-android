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

package com.algorand.backup.di

import com.algorand.backup.data.mapper.SyncStateCacheMapper
import com.algorand.backup.data.model.SyncStateCacheModel
import com.algorand.backup.data.repository.DefaultSyncStateRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object BackupModule {

    private const val SYNC_STATE_CACHE_KEY = "backup_sync_state"

    @Singleton
    @Provides
    fun provideSyncStateRepository(
        cacheProvider: PersistentCacheProvider,
        mapper: SyncStateCacheMapper
    ): SyncStateRepository {
        return DefaultSyncStateRepository(
            persistentCache = cacheProvider.getPersistentCache(
                type = SyncStateCacheModel::class.java,
                key = SYNC_STATE_CACHE_KEY
            ),
            mapper = mapper
        )
    }
}

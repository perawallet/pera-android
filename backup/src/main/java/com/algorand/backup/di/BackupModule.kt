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

import com.algorand.backup.data.mapper.DeltaEntryResponseMapper
import com.algorand.backup.data.mapper.ManifestResponseMapper
import com.algorand.backup.data.mapper.SyncStateCacheMapper
import com.algorand.backup.data.model.SyncStateCacheModel
import com.algorand.backup.data.repository.DefaultBackupRepository
import com.algorand.backup.data.repository.DefaultSyncStateRepository
import com.algorand.backup.data.service.BackupApiService
import com.algorand.backup.data.service.BackupAuthInterceptor
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.backup.domain.usecase.DeleteBackupItem
import com.algorand.backup.domain.usecase.DeleteBackupItemUseCase
import com.algorand.backup.domain.usecase.PullBackupSync
import com.algorand.backup.domain.usecase.PullBackupSyncUseCase
import com.algorand.backup.domain.usecase.PushBackupSync
import com.algorand.backup.domain.usecase.PushBackupSyncUseCase
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object BackupModule {

    private const val SYNC_STATE_CACHE_KEY = "backup_sync_state"
    private const val TIMEOUT_SECONDS = 60L

    // TODO: Replace with actual backup service base URL
    private const val BACKUP_BASE_URL = "https://backup.placeholder.perawallet.app/"

    @Provides
    @Singleton
    fun provideBackupHttpClient(
        backupAuthInterceptor: BackupAuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(backupAuthInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideBackupApiService(
        backupHttpClient: OkHttpClient,
        gson: Gson
    ): BackupApiService {
        return Retrofit.Builder()
            .baseUrl(BACKUP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(backupHttpClient)
            .build()
            .create(BackupApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBackupRepository(
        backupApiService: BackupApiService,
        manifestMapper: ManifestResponseMapper,
        deltaMapper: DeltaEntryResponseMapper
    ): BackupRepository {
        return DefaultBackupRepository(backupApiService, manifestMapper, deltaMapper)
    }

    @Provides
    @Singleton
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

    @Provides
    fun providePullBackupSync(useCase: PullBackupSyncUseCase): PullBackupSync = useCase

    @Provides
    fun providePushBackupSync(useCase: PushBackupSyncUseCase): PushBackupSync = useCase

    @Provides
    fun provideDeleteBackupItem(useCase: DeleteBackupItemUseCase): DeleteBackupItem = useCase
}

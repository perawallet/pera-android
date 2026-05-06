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

import com.algorand.backup.account.domain.mapper.AddressBackupPayloadMapper
import com.algorand.backup.account.domain.mapper.DefaultAddressBackupPayloadMapper
import com.algorand.backup.account.domain.mapper.DefaultSecretsBackupPayloadMapper
import com.algorand.backup.account.domain.mapper.SecretsBackupPayloadMapper
import com.algorand.backup.contact.domain.mapper.ContactBackupPayloadMapper
import com.algorand.backup.contact.domain.mapper.DefaultContactBackupPayloadMapper
import com.algorand.backup.contact.domain.usecase.ContactBackupItemObserver
import com.algorand.backup.data.mapper.SyncStateCacheMapper
import com.algorand.backup.data.model.BackupAuthCredentialsCacheData
import com.algorand.backup.data.model.BackupSessionCacheData
import com.algorand.backup.data.repository.DefaultBackupAuthCredentialsRepository
import com.algorand.backup.domain.repository.BackupAuthCredentialsRepository
import com.algorand.backup.data.model.SyncStateCacheModel
import com.algorand.backup.data.model.BackupSnapshotCacheData
import com.algorand.backup.data.repository.DefaultBackupSessionRepository
import com.algorand.backup.data.repository.DefaultBackupRepository
import com.algorand.backup.data.repository.DefaultBackupSnapshotRepository
import com.algorand.backup.data.repository.DefaultBackupWebSocketRepository
import com.algorand.backup.data.repository.DefaultLocalBackupDataImporter
import com.algorand.backup.data.repository.DefaultLocalBackupDataProvider
import com.algorand.backup.data.repository.DefaultSyncStateRepository
import com.algorand.backup.data.service.BackupApiService
import com.algorand.backup.data.service.BackupAuthInterceptor
import com.algorand.backup.data.service.BackupWebSocketUrlBuilder
import com.algorand.backup.data.service.DefaultBackupWebSocketUrlBuilder
import com.algorand.backup.domain.repository.BackupSessionRepository
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.BackupSnapshotRepository
import com.algorand.backup.domain.repository.BackupWebSocketRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.backup.domain.security.ArgonKeyManager
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.backup.domain.security.BackupIdManager
import com.algorand.backup.domain.security.BackupKeyDerivationManager
import com.algorand.backup.domain.security.BackupRequestSigner
import com.algorand.backup.domain.security.DefaultArgonKeyManager
import com.algorand.backup.domain.security.DefaultBackupEncryptionManager
import com.algorand.backup.domain.security.DefaultBackupIdManager
import com.algorand.backup.domain.security.DefaultBackupKeyDerivationManager
import com.algorand.backup.domain.security.DefaultBackupRequestSigner
import com.algorand.backup.domain.security.DefaultEd25519KeyManager
import com.algorand.backup.domain.security.DefaultHkdfKeyManager
import com.algorand.backup.domain.security.DefaultNonceGenerator
import com.algorand.backup.domain.security.DefaultPeraAndroidKeyStore
import com.algorand.backup.domain.security.DefaultPeraCipher
import com.algorand.backup.domain.security.DefaultSaltGenerator
import com.algorand.backup.domain.security.Ed25519KeyManager
import com.algorand.backup.domain.security.HkdfKeyManager
import com.algorand.backup.domain.security.NonceGenerator
import com.algorand.backup.domain.security.PeraAndroidKeyStore
import com.algorand.backup.domain.security.PeraCipher
import com.algorand.backup.domain.security.SaltGenerator
import com.algorand.backup.domain.usecase.AccountBackupItemObserver
import com.algorand.backup.domain.usecase.AdvanceBackupSyncCursor
import com.algorand.backup.domain.usecase.AdvanceBackupSyncCursorUseCase
import com.algorand.backup.domain.usecase.BackupItemObserver
import com.algorand.backup.domain.usecase.BackupSyncManager
import com.algorand.backup.domain.usecase.BackupSyncStateUpdater
import com.algorand.backup.domain.usecase.ClearBackupAuthCredentials
import com.algorand.backup.domain.usecase.ClearBackupSession
import com.algorand.backup.domain.usecase.CommitPushedItemsToSnapshot
import com.algorand.backup.domain.usecase.CommitPushedItemsToSnapshotUseCase
import com.algorand.backup.domain.usecase.ConnectBackupWebSocket
import com.algorand.backup.domain.usecase.CreateBackup
import com.algorand.backup.domain.usecase.CreateBackupUseCase
import com.algorand.backup.domain.usecase.DecryptBackupPayloads
import com.algorand.backup.domain.usecase.DecryptBackupPayloadsUseCase
import com.algorand.backup.domain.usecase.AddAccountToBackup
import com.algorand.backup.domain.usecase.DefaultAddAccountToBackup
import com.algorand.backup.domain.usecase.DefaultBackupSyncStateUpdater
import com.algorand.backup.domain.usecase.DefaultDeleteAccountFromBackup
import com.algorand.backup.domain.usecase.DefaultResolveAddedAccountBackupKeys
import com.algorand.backup.domain.usecase.DeleteAccountFromBackup
import com.algorand.backup.domain.usecase.DeleteBackup
import com.algorand.backup.domain.usecase.DeleteBackupItem
import com.algorand.backup.domain.usecase.DeleteBackupItemUseCase
import com.algorand.backup.domain.usecase.DeleteBackupUseCase
import com.algorand.backup.domain.usecase.DeletePendingBackupItems
import com.algorand.backup.domain.usecase.DeletePendingBackupItemsUseCase
import com.algorand.backup.domain.usecase.DisableBackup
import com.algorand.backup.domain.usecase.DisableBackupUseCase
import com.algorand.backup.domain.usecase.DisconnectBackupWebSocket
import com.algorand.backup.domain.usecase.EncryptBackupPayloads
import com.algorand.backup.domain.usecase.EncryptBackupPayloadsUseCase
import com.algorand.backup.domain.usecase.EvictDeletedItemsFromSnapshot
import com.algorand.backup.domain.usecase.EvictDeletedItemsFromSnapshotUseCase
import com.algorand.backup.domain.usecase.FetchAndImportBackupItems
import com.algorand.backup.domain.usecase.FetchAndImportBackupItemsUseCase
import com.algorand.backup.domain.usecase.DefaultGetLatestSync
import com.algorand.backup.domain.usecase.DefaultSaveBackupSyncResult
import com.algorand.backup.domain.usecase.GetAddressBackupSnapshot
import com.algorand.backup.domain.usecase.GetBackupDeviceId
import com.algorand.backup.domain.usecase.GetBackupId
import com.algorand.backup.domain.usecase.GetBackupWebSocketEvents
import com.algorand.backup.domain.usecase.GetContactBackupSnapshot
import com.algorand.backup.domain.usecase.GetLatestSync
import com.algorand.backup.domain.usecase.SaveBackupSyncResult
import com.algorand.backup.domain.usecase.HasBackup
import com.algorand.backup.domain.usecase.LocalBackupDataImporter
import com.algorand.backup.domain.usecase.LocalBackupDataProvider
import com.algorand.backup.domain.usecase.PreparePushPayloads
import com.algorand.backup.domain.usecase.PreparePushPayloadsUseCase
import com.algorand.backup.domain.usecase.PullAndImportSync
import com.algorand.backup.domain.usecase.PullAndImportSyncUseCase
import com.algorand.backup.domain.usecase.PullBackupSync
import com.algorand.backup.domain.usecase.PullBackupSyncUseCase
import com.algorand.backup.domain.usecase.PushBackupSync
import com.algorand.backup.domain.usecase.PushBackupSyncUseCase
import com.algorand.backup.domain.usecase.PushDirtyBackupItems
import com.algorand.backup.domain.usecase.PushDirtyBackupItemsUseCase
import com.algorand.backup.domain.usecase.ReactivateBackupItem
import com.algorand.backup.domain.usecase.ReactivateBackupItemUseCase
import com.algorand.backup.domain.usecase.RegisterBackup
import com.algorand.backup.domain.usecase.ResolveAddedAccountBackupKeys
import com.algorand.backup.domain.usecase.RestoreBackup
import com.algorand.backup.domain.usecase.RestoreBackupUseCase
import com.algorand.backup.domain.usecase.RevealBackupAuthCredentials
import com.algorand.backup.domain.usecase.StoreBackupAuthCredentials
import com.algorand.backup.domain.usecase.StoreBackupSession
import com.algorand.backup.domain.usecase.SyncBackup
import com.algorand.backup.domain.usecase.SyncBackupUseCase
import com.algorand.backup.domain.usecase.UseBackupPrivateKey
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object BackupModule {

    private const val SYNC_STATE_CACHE_KEY = "backup_sync_state"
    private const val SESSION_CACHE_KEY = "backup_session"
    private const val AUTH_CREDENTIALS_CACHE_KEY = "backup_auth_credentials"
    private const val SNAPSHOT_CACHE_KEY = "backup_snapshot"
    private const val TIMEOUT_SECONDS = 60L
    private const val BACKUP_BASE_URL = "http://10.0.2.2:3011/api/v3/"

    @Provides
    @Singleton
    fun providePeraAndroidKeyStore(keyStore: DefaultPeraAndroidKeyStore): PeraAndroidKeyStore = keyStore

    @Provides
    @Singleton
    fun providePeraCipher(cipher: DefaultPeraCipher): PeraCipher = cipher

    @Provides
    @Singleton
    fun provideBackupSessionRepository(
        cacheProvider: PersistentCacheProvider,
        peraAndroidKeyStore: PeraAndroidKeyStore,
        peraCipher: PeraCipher
    ): BackupSessionRepository {
        return DefaultBackupSessionRepository(
            persistentCache = cacheProvider.getPersistentCache(
                type = BackupSessionCacheData::class.java,
                key = SESSION_CACHE_KEY
            ),
            peraAndroidKeyStore = peraAndroidKeyStore,
            peraCipher = peraCipher
        )
    }

    @Provides
    @Singleton
    fun provideBackupAuthCredentialsRepository(
        cacheProvider: PersistentCacheProvider,
        peraAndroidKeyStore: PeraAndroidKeyStore,
        peraCipher: PeraCipher
    ): BackupAuthCredentialsRepository {
        return DefaultBackupAuthCredentialsRepository(
            persistentCache = cacheProvider.getPersistentCache(
                type = BackupAuthCredentialsCacheData::class.java,
                key = AUTH_CREDENTIALS_CACHE_KEY
            ),
            peraAndroidKeyStore = peraAndroidKeyStore,
            peraCipher = peraCipher
        )
    }

    @Provides
    @Singleton
    fun provideNonceGenerator(generator: DefaultNonceGenerator): NonceGenerator = generator

    @Provides
    @Singleton
    fun provideSaltGenerator(generator: DefaultSaltGenerator): SaltGenerator = generator

    @Provides
    @Singleton
    fun provideBackupRequestSigner(signer: DefaultBackupRequestSigner): BackupRequestSigner = signer

    @Provides
    @Singleton
    fun provideBackupHttpClient(backupAuthInterceptor: BackupAuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(backupAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideBackupApiService(backupHttpClient: OkHttpClient, gson: Gson): BackupApiService {
        return Retrofit.Builder()
            .baseUrl(BACKUP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(backupHttpClient)
            .build()
            .create(BackupApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBackupRepository(repository: DefaultBackupRepository): BackupRepository = repository

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
    @Singleton
    fun provideBackupSnapshotRepository(
        cacheProvider: PersistentCacheProvider,
        addressMapper: AddressBackupPayloadMapper
    ): BackupSnapshotRepository {
        return DefaultBackupSnapshotRepository(
            persistentCache = cacheProvider.getPersistentCache(
                type = BackupSnapshotCacheData::class.java,
                key = SNAPSHOT_CACHE_KEY
            ),
            addressMapper = addressMapper
        )
    }

    @Provides
    fun provideGetAddressBackupSnapshot(
        repository: BackupSnapshotRepository
    ): GetAddressBackupSnapshot = GetAddressBackupSnapshot(repository::getAddressPayloads)

    @Provides
    fun provideGetContactBackupSnapshot(
        repository: BackupSnapshotRepository
    ): GetContactBackupSnapshot = GetContactBackupSnapshot(repository::getContactPayloads)

    @Provides
    fun provideGetLatestSync(useCase: DefaultGetLatestSync): GetLatestSync = useCase

    @Provides
    fun provideSaveBackupSyncResult(useCase: DefaultSaveBackupSyncResult): SaveBackupSyncResult = useCase

    @Provides
    fun provideArgonKeyManager(manager: DefaultArgonKeyManager): ArgonKeyManager = manager

    @Provides
    fun provideHkdfKeyManager(manager: DefaultHkdfKeyManager): HkdfKeyManager = manager

    @Provides
    fun provideEd25519KeyManager(manager: DefaultEd25519KeyManager): Ed25519KeyManager = manager

    @Provides
    fun provideBackupIdManager(manager: DefaultBackupIdManager): BackupIdManager = manager

    @Provides
    fun provideBackupKeyDerivationManager(
        manager: DefaultBackupKeyDerivationManager
    ): BackupKeyDerivationManager = manager

    @Provides
    fun provideBackupEncryptionManager(manager: DefaultBackupEncryptionManager): BackupEncryptionManager = manager

    @Provides
    fun provideRegisterBackup(repository: BackupRepository): RegisterBackup = RegisterBackup(repository::register)

    @Provides
    fun provideCreateBackup(useCase: CreateBackupUseCase): CreateBackup = useCase

    @Provides
    fun provideRestoreBackup(useCase: RestoreBackupUseCase): RestoreBackup = useCase

    @Provides
    fun providePullBackupSync(useCase: PullBackupSyncUseCase): PullBackupSync = useCase

    @Provides
    fun providePushDirtyBackupItems(useCase: PushDirtyBackupItemsUseCase): PushDirtyBackupItems = useCase

    @Provides
    fun provideDeletePendingBackupItems(useCase: DeletePendingBackupItemsUseCase): DeletePendingBackupItems = useCase

    @Provides
    fun provideCommitPushedItemsToSnapshot(
        useCase: CommitPushedItemsToSnapshotUseCase
    ): CommitPushedItemsToSnapshot = useCase

    @Provides
    fun provideEvictDeletedItemsFromSnapshot(
        useCase: EvictDeletedItemsFromSnapshotUseCase
    ): EvictDeletedItemsFromSnapshot = useCase

    @Provides
    fun provideAdvanceBackupSyncCursor(
        useCase: AdvanceBackupSyncCursorUseCase
    ): AdvanceBackupSyncCursor = useCase

    @Provides
    fun providePushBackupSync(useCase: PushBackupSyncUseCase): PushBackupSync = useCase

    @Provides
    fun provideDeleteBackupItem(useCase: DeleteBackupItemUseCase): DeleteBackupItem = useCase

    @Provides
    fun provideDeleteAccountFromBackup(useCase: DefaultDeleteAccountFromBackup): DeleteAccountFromBackup = useCase

    @Provides
    fun provideAddAccountToBackup(useCase: DefaultAddAccountToBackup): AddAccountToBackup = useCase

    @Provides
    fun provideResolveAddedAccountBackupKeys(
        useCase: DefaultResolveAddedAccountBackupKeys
    ): ResolveAddedAccountBackupKeys = useCase

    @Provides
    fun provideReactivateBackupItem(useCase: ReactivateBackupItemUseCase): ReactivateBackupItem = useCase

    @Provides
    fun provideEncryptBackupPayloads(useCase: EncryptBackupPayloadsUseCase): EncryptBackupPayloads = useCase

    @Provides
    fun provideDecryptBackupPayloads(useCase: DecryptBackupPayloadsUseCase): DecryptBackupPayloads = useCase

    @Provides
    fun provideHasBackup(repository: BackupSessionRepository): HasBackup = HasBackup(repository::hasSession)

    @Provides
    fun provideGetBackupId(repository: BackupSessionRepository): GetBackupId = GetBackupId(repository::getBackupId)

    @Provides
    fun provideGetBackupDeviceId(
        repository: BackupSessionRepository
    ): GetBackupDeviceId = GetBackupDeviceId(repository::getDeviceId)

    @Provides
    fun provideStoreBackupSession(
        repository: BackupSessionRepository
    ): StoreBackupSession = StoreBackupSession(repository::storeSession)

    @Provides
    fun provideUseBackupPrivateKey(repository: BackupSessionRepository): UseBackupPrivateKey {
        return object : UseBackupPrivateKey {
            override fun <T : Any> invoke(block: (com.algorand.backup.domain.model.SensitiveBytes) -> T) =
                repository.usePrivateKey(block)
        }
    }

    @Provides
    fun provideClearBackupSession(repository: BackupSessionRepository): ClearBackupSession {
        return ClearBackupSession(repository::clearSession)
    }

    @Provides
    fun provideStoreBackupAuthCredentials(
        repository: BackupAuthCredentialsRepository
    ): StoreBackupAuthCredentials = StoreBackupAuthCredentials(repository::storeCredentials)

    @Provides
    fun provideRevealBackupAuthCredentials(
        repository: BackupAuthCredentialsRepository
    ): RevealBackupAuthCredentials {
        return object : RevealBackupAuthCredentials {
            override fun <T : Any> invoke(
                block: (
                    com.algorand.backup.domain.model.BackupId,
                    com.algorand.backup.domain.model.SensitiveBytes,
                    ByteArray
                ) -> T
            ): PeraResult<T> = repository.useCredentials(block)
        }
    }

    @Provides
    fun provideClearBackupAuthCredentials(
        repository: BackupAuthCredentialsRepository
    ): ClearBackupAuthCredentials {
        return ClearBackupAuthCredentials(repository::clearCredentials)
    }

    @Provides
    fun provideDisableBackup(useCase: DisableBackupUseCase): DisableBackup = useCase

    @Provides
    fun provideDeleteBackup(useCase: DeleteBackupUseCase): DeleteBackup = useCase

    @Provides
    fun provideAddressBackupPayloadMapper(
        mapper: DefaultAddressBackupPayloadMapper
    ): AddressBackupPayloadMapper = mapper

    @Provides
    fun provideSecretsBackupPayloadMapper(
        mapper: DefaultSecretsBackupPayloadMapper
    ): SecretsBackupPayloadMapper = mapper

    @Provides
    fun provideContactBackupPayloadMapper(
        mapper: DefaultContactBackupPayloadMapper
    ): ContactBackupPayloadMapper = mapper

    @Provides
    fun providePreparePushPayloads(useCase: PreparePushPayloadsUseCase): PreparePushPayloads = useCase

    @Provides
    fun provideSyncBackup(useCase: SyncBackupUseCase): SyncBackup = useCase

    @Provides
    fun providePullAndImportSync(useCase: PullAndImportSyncUseCase): PullAndImportSync = useCase

    @Provides
    fun provideFetchAndImportBackupItems(
        useCase: FetchAndImportBackupItemsUseCase
    ): FetchAndImportBackupItems = useCase

    @Provides
    fun provideLocalBackupDataProvider(provider: DefaultLocalBackupDataProvider): LocalBackupDataProvider = provider

    @Provides
    fun provideLocalBackupDataImporter(importer: DefaultLocalBackupDataImporter): LocalBackupDataImporter = importer

    @Provides
    fun provideBackupSyncStateUpdater(updater: DefaultBackupSyncStateUpdater): BackupSyncStateUpdater = updater

    @Provides
    @Singleton
    fun provideBackupWebSocketUrlBuilder(
        backupSessionRepository: BackupSessionRepository,
        requestSigner: BackupRequestSigner
    ): BackupWebSocketUrlBuilder {
        return DefaultBackupWebSocketUrlBuilder(
            backupSessionRepository = backupSessionRepository,
            requestSigner = requestSigner,
            baseUrl = BACKUP_BASE_URL
        )
    }

    @Provides
    @Singleton
    fun provideBackupWebSocketRepository(
        repository: DefaultBackupWebSocketRepository
    ): BackupWebSocketRepository = repository

    @Provides
    fun provideConnectBackupWebSocket(
        repository: BackupWebSocketRepository
    ): ConnectBackupWebSocket = ConnectBackupWebSocket(repository::connect)

    @Provides
    fun provideDisconnectBackupWebSocket(
        repository: BackupWebSocketRepository
    ): DisconnectBackupWebSocket = DisconnectBackupWebSocket(repository::disconnect)

    @Provides
    fun provideGetBackupWebSocketEvents(
        repository: BackupWebSocketRepository
    ): GetBackupWebSocketEvents = GetBackupWebSocketEvents { repository.events }

    @Provides
    @IntoSet
    fun provideAccountBackupItemObserver(
        observer: AccountBackupItemObserver
    ): BackupItemObserver = observer

    @Provides
    @IntoSet
    fun provideContactBackupItemObserver(
        observer: ContactBackupItemObserver
    ): BackupItemObserver = observer

    @Provides
    @Singleton
    fun provideBackupSyncManager(
        syncBackup: SyncBackup,
        pullAndImportSync: PullAndImportSync,
        hasBackup: HasBackup,
        connectBackupWebSocket: ConnectBackupWebSocket,
        disconnectBackupWebSocket: DisconnectBackupWebSocket,
        getBackupWebSocketEvents: GetBackupWebSocketEvents,
        itemObservers: Set<@JvmSuppressWildcards BackupItemObserver>
    ): BackupSyncManager {
        return BackupSyncManager(
            syncBackup = syncBackup,
            pullAndImportSync = pullAndImportSync,
            hasBackup = hasBackup,
            connectBackupWebSocket = connectBackupWebSocket,
            disconnectBackupWebSocket = disconnectBackupWebSocket,
            getBackupWebSocketEvents = getBackupWebSocketEvents,
            itemObservers = itemObservers,
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )
    }
}

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

package com.algorand.backup.domain.usecase

import com.algorand.backup.domain.mapper.SyncItemStateMapper
import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupManifest
import com.algorand.backup.domain.model.DerivedKeyMaterial
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.KeyDerivationInput
import com.algorand.backup.domain.model.RestoredBackup
import com.algorand.backup.domain.model.SyncState
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.backup.domain.security.BackupKeyDerivationManager
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class RestoreBackupUseCase @Inject constructor(
    private val keyDerivationManager: BackupKeyDerivationManager,
    private val encryptionManager: BackupEncryptionManager,
    private val backupRepository: BackupRepository,
    private val syncStateRepository: SyncStateRepository,
    private val syncItemStateMapper: SyncItemStateMapper,
    private val storeBackupCredentials: StoreBackupCredentials,
    private val clearBackupCredentials: ClearBackupCredentials,
    private val pullAndImportSync: PullAndImportSync
) : RestoreBackup {

    override suspend fun invoke(
        mnemonic: String,
        salt: ByteArray,
        argon2idConfig: Argon2idConfig,
        deviceId: DeviceId
    ): PeraResult<RestoredBackup> {
        val keyMaterial = deriveKeys(mnemonic, salt, argon2idConfig)
            ?: return PeraResult.Error(IllegalStateException("Key derivation failed"))

        return keyMaterial.use { restore(keyMaterial, deviceId) }
    }

    private fun deriveKeys(mnemonic: String, salt: ByteArray, argon2idConfig: Argon2idConfig): DerivedKeyMaterial? {
        val input = KeyDerivationInput(mnemonic = mnemonic, salt = salt, argon2idConfig = argon2idConfig)
        return keyDerivationManager.deriveKeys(input).getDataOrNull()
    }

    private suspend fun restore(keyMaterial: DerivedKeyMaterial, deviceId: DeviceId): PeraResult<RestoredBackup> {
        val credentialResult = storeBackupCredentials(keyMaterial.backupId, deviceId, keyMaterial.authPrivateKey)
        if (credentialResult is PeraResult.Error) {
            return PeraResult.Error(credentialResult.exception)
        }

        val manifest = backupRepository.getManifest(keyMaterial.backupId).getDataOrNull()
            ?: return rollbackWithError(IllegalStateException("Failed to fetch manifest"))

        val importResult = encryptionManager.importKey(keyMaterial.encryptionKey)
        if (importResult is PeraResult.Error) {
            return rollbackWithError(importResult.exception)
        }

        val syncState = createSyncState(keyMaterial.backupId, manifest)
        syncStateRepository.saveSyncState(syncState)

        pullAndImportSync()

        return PeraResult.Success(RestoredBackup(backupId = keyMaterial.backupId, syncState = syncState))
    }

    private fun createSyncState(backupId: BackupId, manifest: BackupManifest): SyncState {
        val syncItems = manifest.items.mapValues { (_, manifestItem) ->
            syncItemStateMapper.mapFromManifestItem(manifestItem)
        }
        return SyncState(backupId = backupId, lastKnownBackupHash = null, lastSyncedSeq = 0, items = syncItems)
    }

    private fun <T : Any> rollbackWithError(exception: Exception): PeraResult<T> {
        clearBackupCredentials()
        return PeraResult.Error(exception)
    }
}

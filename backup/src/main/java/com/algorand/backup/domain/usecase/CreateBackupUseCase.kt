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

import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.CreatedBackup
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.KeyDerivationInput
import com.algorand.backup.domain.model.SyncState
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.backup.domain.security.BackupKeyDerivationManager
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class CreateBackupUseCase @Inject constructor(
    private val keyDerivationManager: BackupKeyDerivationManager,
    private val registerBackup: RegisterBackup,
    private val encryptionManager: BackupEncryptionManager,
    private val syncStateRepository: SyncStateRepository,
    private val storeBackupCredentials: StoreBackupCredentials
) : CreateBackup {

    override suspend fun invoke(mnemonic: String, deviceId: DeviceId, salt: ByteArray): PeraResult<CreatedBackup> {
        val keyDerivationInput = KeyDerivationInput(mnemonic, salt, Argon2idConfig.DEFAULT)
        val keyMaterial = when (val result = keyDerivationManager.deriveKeys(keyDerivationInput)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> return PeraResult.Error(result.exception)
        }

        return keyMaterial.use {
            val registerResult = registerBackup(keyMaterial, deviceId)
            if (registerResult is PeraResult.Error) {
                return PeraResult.Error(registerResult.exception, registerResult.code)
            }

            val importResult = encryptionManager.importKey(keyMaterial.encryptionKey)
            if (importResult is PeraResult.Error) {
                return PeraResult.Error(importResult.exception)
            }

            val storeResult = storeBackupCredentials(keyMaterial.backupId, deviceId, keyMaterial.authPrivateKey)
            if (storeResult is PeraResult.Error) {
                return PeraResult.Error(storeResult.exception)
            }

            initializeSyncState(keyMaterial.backupId)

            PeraResult.Success(CreatedBackup(keyMaterial.backupId, salt, Argon2idConfig.DEFAULT))
        }
    }

    private suspend fun initializeSyncState(backupId: BackupId) {
        val emptySyncState = SyncState(
            backupId = backupId,
            lastKnownBackupHash = null,
            lastSyncedSeq = 0L,
            items = emptyMap()
        )
        syncStateRepository.saveSyncState(emptySyncState)
    }
}

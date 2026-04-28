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

import com.algorand.backup.domain.repository.BackupSnapshotRepository
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.backup.domain.security.BackupEncryptionManager
import javax.inject.Inject

internal class DisableBackupUseCase @Inject constructor(
    private val getBackupId: GetBackupId,
    private val clearBackupCredentials: ClearBackupCredentials,
    private val encryptionManager: BackupEncryptionManager,
    private val syncStateRepository: SyncStateRepository,
    private val backupSnapshotRepository: BackupSnapshotRepository,
    private val backupSyncManager: BackupSyncManager
) : DisableBackup {

    override suspend fun invoke() {
        backupSyncManager.stop()
        getBackupId()?.let { backupId -> syncStateRepository.deleteSyncState(backupId) }
        backupSnapshotRepository.clear()
        encryptionManager.deleteKey()
        clearBackupCredentials()
    }
}

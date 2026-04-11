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

import com.algorand.backup.account.domain.mapper.AddressBackupPayloadMapper
import com.algorand.backup.account.domain.mapper.SecretsBackupPayloadMapper
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.SyncItemState
import com.algorand.backup.domain.repository.SyncStateRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class PreparePushPayloadsUseCase @Inject constructor(
    private val localBackupDataProvider: LocalBackupDataProvider,
    private val syncStateRepository: SyncStateRepository,
    private val addressBackupPayloadMapper: AddressBackupPayloadMapper,
    private val secretsBackupPayloadMapper: SecretsBackupPayloadMapper,
    private val encryptBackupPayloads: EncryptBackupPayloads
) : PreparePushPayloads {

    override suspend fun invoke(backupId: BackupId): PeraResult<Map<BackupItemKey, String>> {
        val existingItems = syncStateRepository.getSyncState(backupId)?.items.orEmpty()
        val plaintextPayloads = collectDirtyPayloads(existingItems)

        if (plaintextPayloads.isEmpty()) return PeraResult.Success(emptyMap())

        markItemsDirty(backupId, plaintextPayloads.keys, existingItems)

        return encryptBackupPayloads(plaintextPayloads)
    }

    private suspend fun collectDirtyPayloads(
        existingItems: Map<BackupItemKey, SyncItemState>
    ): Map<BackupItemKey, ByteArray> {
        val payloads = mutableMapOf<BackupItemKey, ByteArray>()

        for (payload in localBackupDataProvider.getAddressPayloads()) {
            val key = BackupItemKey("$ACCOUNTS_PREFIX${payload.address}")
            if (existingItems.containsKey(key) && existingItems[key]?.isDirty != true) continue
            payloads[key] = addressBackupPayloadMapper.serialize(payload)
        }

        for (payload in localBackupDataProvider.getSecretsPayloads()) {
            val key = BackupItemKey("$SECRETS_PREFIX${payload.address}")
            if (existingItems.containsKey(key) && existingItems[key]?.isDirty != true) continue
            payloads[key] = secretsBackupPayloadMapper.serialize(payload)
        }

        return payloads
    }

    private suspend fun markItemsDirty(
        backupId: BackupId,
        keys: Set<BackupItemKey>,
        existingItems: Map<BackupItemKey, SyncItemState>
    ) {
        keys.forEach { key ->
            val existing = existingItems[key]
            val item = SyncItemState(
                type = BackupItemType.ACCOUNT,
                knownVersion = existing?.knownVersion ?: 0,
                baseVersion = existing?.baseVersion ?: 0,
                isDirty = true,
                status = BackupItemStatus.ACTIVE,
                lastRemoteHash = existing?.lastRemoteHash,
                pendingDelete = false
            )
            syncStateRepository.updateItemState(backupId, key, item)
        }
    }

    private companion object {
        const val ACCOUNTS_PREFIX = "accounts/"
        const val SECRETS_PREFIX = "secrets/"
    }
}

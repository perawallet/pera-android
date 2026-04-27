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

import android.util.Base64
import com.algorand.backup.account.domain.mapper.AddressBackupPayloadMapper
import com.algorand.backup.account.domain.mapper.SecretsBackupPayloadMapper
import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.account.domain.model.SecretsBackupPayload
import com.algorand.backup.contact.domain.mapper.ContactBackupPayloadMapper
import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.backup.contact.domain.usecase.ContactsBackupDataImporter
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.PullSyncResult
import com.algorand.backup.domain.model.SyncBackupResult
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class PullAndImportSyncUseCase @Inject constructor(
    private val getBackupId: GetBackupId,
    private val pullBackupSync: PullBackupSync,
    private val backupRepository: BackupRepository,
    private val encryptionManager: BackupEncryptionManager,
    private val addressBackupPayloadMapper: AddressBackupPayloadMapper,
    private val secretsBackupPayloadMapper: SecretsBackupPayloadMapper,
    private val contactBackupPayloadMapper: ContactBackupPayloadMapper,
    private val localBackupDataImporter: LocalBackupDataImporter,
    private val contactsBackupDataImporter: ContactsBackupDataImporter,
    private val errorLogger: PeraErrorLogger
) : PullAndImportSync {

    override suspend fun invoke(): SyncBackupResult {
        val backupId = getBackupId() ?: return SyncBackupResult.Error(IllegalStateException("No backup ID"))
        return when (val result = pullBackupSync(backupId)) {
            is PullSyncResult.UpToDate -> SyncBackupResult.Success
            is PullSyncResult.Error -> SyncBackupResult.Error(result.exception)
            is PullSyncResult.Updated -> {
                downloadAndImport(backupId, result.updatedKeys)
                SyncBackupResult.Success
            }
        }
    }

    private suspend fun downloadAndImport(backupId: BackupId, keys: List<BackupItemKey>) {
        if (keys.isEmpty()) return

        val downloadResult = backupRepository.batchReadItems(backupId, keys)
        if (downloadResult !is PeraResult.Success) return

        val addressPayloads = mutableListOf<AddressBackupPayload>()
        val secretsPayloads = mutableListOf<SecretsBackupPayload>()
        val contactPayloads = mutableListOf<ContactBackupPayload>()

        for ((key, base64Payload) in downloadResult.data) {
            val encrypted = Base64.decode(base64Payload, Base64.NO_WRAP)
            val decrypted = when (val decryptResult = encryptionManager.decrypt(encrypted, key.value)) {
                is PeraResult.Success -> decryptResult.data
                is PeraResult.Error -> {
                    errorLogger.logError(decryptResult.exception)
                    continue
                }
            }

            when {
                key.isAddress() -> addressBackupPayloadMapper.deserialize(decrypted)?.let { addressPayloads.add(it) }
                key.isSecrets() -> secretsBackupPayloadMapper.deserialize(decrypted)?.let { secretsPayloads.add(it) }
                key.isContact() -> contactBackupPayloadMapper.deserialize(decrypted)?.let { contactPayloads.add(it) }
            }
        }

        if (secretsPayloads.isNotEmpty()) localBackupDataImporter.importSecrets(secretsPayloads, addressPayloads)
        if (addressPayloads.isNotEmpty()) localBackupDataImporter.importAddresses(addressPayloads)
        if (contactPayloads.isNotEmpty()) contactsBackupDataImporter.importContacts(contactPayloads)
    }
}

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
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.repository.BackupSnapshotRepository
import com.algorand.backup.domain.security.BackupEncryptionManager
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class FetchAndImportBackupItemsUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val encryptionManager: BackupEncryptionManager,
    private val addressBackupPayloadMapper: AddressBackupPayloadMapper,
    private val secretsBackupPayloadMapper: SecretsBackupPayloadMapper,
    private val contactBackupPayloadMapper: ContactBackupPayloadMapper,
    private val localBackupDataImporter: LocalBackupDataImporter,
    private val contactsBackupDataImporter: ContactsBackupDataImporter,
    private val backupSnapshotRepository: BackupSnapshotRepository,
    private val errorLogger: PeraErrorLogger
) : FetchAndImportBackupItems {

    override suspend fun invoke(backupId: BackupId, keys: List<BackupItemKey>): PeraResult<Unit> {
        if (keys.isEmpty()) return PeraResult.Success(Unit)

        return when (val downloadResult = backupRepository.batchReadItems(backupId, keys)) {
            is PeraResult.Error -> PeraResult.Error(downloadResult.exception, downloadResult.code)
            is PeraResult.Success -> importPayloads(downloadResult.data)
        }
    }

    private suspend fun importPayloads(data: Map<BackupItemKey, String>): PeraResult<Unit> {
        return try {
            val payloads = decryptAndCategorize(data)
            updateSnapshot(payloads)
            importToLocal(payloads)
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            errorLogger.logError(e)
            PeraResult.Error(e)
        }
    }

    private fun decryptAndCategorize(data: Map<BackupItemKey, String>): DecryptedPayloads {
        val addresses = mutableListOf<AddressBackupPayload>()
        val secrets = mutableListOf<SecretsBackupPayload>()
        val contacts = mutableListOf<ContactBackupPayload>()
        for ((key, base64Payload) in data) {
            val decrypted = decryptOrNull(key, base64Payload) ?: continue
            when {
                key.isAddress() -> addressBackupPayloadMapper.deserialize(decrypted)?.let(addresses::add)
                key.isSecrets() -> secretsBackupPayloadMapper.deserialize(decrypted)?.let(secrets::add)
                key.isContact() -> contactBackupPayloadMapper.deserialize(decrypted)?.let(contacts::add)
            }
        }
        return DecryptedPayloads(addresses, secrets, contacts)
    }

    private fun decryptOrNull(key: BackupItemKey, base64Payload: String): ByteArray? {
        val encrypted = Base64.decode(base64Payload, Base64.NO_WRAP)
        return when (val result = encryptionManager.decrypt(encrypted, key.value)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> {
                errorLogger.logError(result.exception)
                null
            }
        }
    }

    private suspend fun updateSnapshot(payloads: DecryptedPayloads) {
        if (payloads.addresses.isNotEmpty()) backupSnapshotRepository.upsertAddressPayloads(payloads.addresses)
        if (payloads.contacts.isNotEmpty()) backupSnapshotRepository.upsertContactPayloads(payloads.contacts)
    }

    private suspend fun importToLocal(payloads: DecryptedPayloads) {
        if (payloads.secrets.isNotEmpty()) {
            localBackupDataImporter.importSecrets(payloads.secrets, payloads.addresses)
        }
        if (payloads.addresses.isNotEmpty()) localBackupDataImporter.importAddresses(payloads.addresses)
        if (payloads.contacts.isNotEmpty()) contactsBackupDataImporter.importContacts(payloads.contacts)
    }

    private data class DecryptedPayloads(
        val addresses: List<AddressBackupPayload>,
        val secrets: List<SecretsBackupPayload>,
        val contacts: List<ContactBackupPayload>
    )
}

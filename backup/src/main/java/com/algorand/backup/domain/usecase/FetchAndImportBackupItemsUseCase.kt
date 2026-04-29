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
import com.algorand.wallet.account.local.domain.usecase.IsThereAnySeedWithFirstAddress
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
    private val isThereAnySeedWithFirstAddress: IsThereAnySeedWithFirstAddress,
    private val errorLogger: PeraErrorLogger
) : FetchAndImportBackupItems {

    override suspend fun invoke(backupId: BackupId, keys: List<BackupItemKey>): PeraResult<Set<String>> {
        if (keys.isEmpty()) return PeraResult.Success(emptySet())

        return when (val downloadResult = backupRepository.batchReadItems(backupId, keys)) {
            is PeraResult.Error -> PeraResult.Error(downloadResult.exception, downloadResult.code)
            is PeraResult.Success -> importPayloads(backupId, downloadResult.data)
        }
    }

    private suspend fun importPayloads(
        backupId: BackupId,
        data: Map<BackupItemKey, String>
    ): PeraResult<Set<String>> {
        return try {
            val payloads = decryptAndCategorize(data)
            val resolvedSecrets = ensureParentSeedsAvailable(backupId, payloads)
            val combined = payloads.copy(secrets = payloads.secrets + resolvedSecrets)
            val importedAddresses = importToLocal(combined)
            updateSnapshot(combined, importedAddresses)
            PeraResult.Success(importedAddresses)
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

    private suspend fun ensureParentSeedsAvailable(
        backupId: BackupId,
        payloads: DecryptedPayloads
    ): List<SecretsBackupPayload.HdSeed> {
        val hdKeys = payloads.addresses.filterIsInstance<AddressBackupPayload.HdKey>()
        if (hdKeys.isEmpty()) return emptyList()

        val incomingSeedAddresses = payloads.secrets.filterIsInstance<SecretsBackupPayload.HdSeed>()
            .map { it.address }
            .toSet()
        val candidateAddresses = hdKeys
            .map { it.seedFirstDerivedAddress }
            .filter { it.isNotEmpty() && it !in incomingSeedAddresses }
            .toSet()
        val missingSeedAddresses = candidateAddresses.filterNot { isThereAnySeedWithFirstAddress(it) }

        if (missingSeedAddresses.isEmpty()) return emptyList()

        return missingSeedAddresses.mapNotNull { address ->
            fetchSeedSecret(backupId, address)
        }
    }

    private suspend fun fetchSeedSecret(backupId: BackupId, address: String): SecretsBackupPayload.HdSeed? {
        val key = BackupItemKey.secrets(address)
        val base64Payload = when (val result = backupRepository.getItem(backupId, key)) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> {
                errorLogger.logError(result.exception)
                return null
            }
        }
        val decrypted = decryptOrNull(key, base64Payload) ?: return null
        val payload = secretsBackupPayloadMapper.deserialize(decrypted) ?: return null
        return payload as? SecretsBackupPayload.HdSeed
    }

    private suspend fun importToLocal(payloads: DecryptedPayloads): Set<String> {
        if (payloads.secrets.isNotEmpty()) {
            localBackupDataImporter.importSecrets(payloads.secrets, payloads.addresses)
        }
        val importedAddresses = if (payloads.addresses.isNotEmpty()) {
            localBackupDataImporter.importAddresses(payloads.addresses)
        } else {
            emptySet()
        }
        if (payloads.contacts.isNotEmpty()) contactsBackupDataImporter.importContacts(payloads.contacts)
        return importedAddresses
    }

    private suspend fun updateSnapshot(payloads: DecryptedPayloads, importedAddresses: Set<String>) {
        val addressPayloadsToSnapshot = payloads.addresses.filter { it.address in importedAddresses }
        if (addressPayloadsToSnapshot.isNotEmpty()) {
            backupSnapshotRepository.upsertAddressPayloads(addressPayloadsToSnapshot)
        }
        if (payloads.contacts.isNotEmpty()) backupSnapshotRepository.upsertContactPayloads(payloads.contacts)
    }

    private data class DecryptedPayloads(
        val addresses: List<AddressBackupPayload>,
        val secrets: List<SecretsBackupPayload>,
        val contacts: List<ContactBackupPayload>
    )
}

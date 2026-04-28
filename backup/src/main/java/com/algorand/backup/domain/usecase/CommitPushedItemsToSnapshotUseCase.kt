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

import com.algorand.backup.contact.domain.usecase.ContactsBackupDataProvider
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.repository.BackupSnapshotRepository
import javax.inject.Inject

internal class CommitPushedItemsToSnapshotUseCase @Inject constructor(
    private val backupSnapshotRepository: BackupSnapshotRepository,
    private val localBackupDataProvider: LocalBackupDataProvider,
    private val contactsBackupDataProvider: ContactsBackupDataProvider
) : CommitPushedItemsToSnapshot {

    override suspend fun invoke(succeededKeys: List<BackupItemKey>) {
        if (succeededKeys.isEmpty()) return

        commitAddresses(succeededKeys)
        commitContacts(succeededKeys)
    }

    private suspend fun commitAddresses(succeededKeys: List<BackupItemKey>) {
        val succeededAddresses = succeededKeys
            .filter { it.isAddress() }
            .mapTo(hashSetOf()) { it.value.removePrefix(BackupItemKey.ACCOUNTS_PREFIX) }
        if (succeededAddresses.isEmpty()) return

        val addressesToUpsert = localBackupDataProvider.getAddressPayloads()
            .filter { it.address in succeededAddresses }
        backupSnapshotRepository.upsertAddressPayloads(addressesToUpsert)
    }

    private suspend fun commitContacts(succeededKeys: List<BackupItemKey>) {
        val succeededContacts = succeededKeys
            .filter { it.isContact() }
            .mapTo(hashSetOf()) { it.value.removePrefix(BackupItemKey.CONTACTS_PREFIX) }
        if (succeededContacts.isEmpty()) return

        val contactsToUpsert = contactsBackupDataProvider.getContactPayloads()
            .filter { it.address in succeededContacts }
        backupSnapshotRepository.upsertContactPayloads(contactsToUpsert)
    }
}

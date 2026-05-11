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

import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.repository.BackupSnapshotRepository
import javax.inject.Inject

internal class EvictDeletedItemsFromSnapshotUseCase @Inject constructor(
    private val backupSnapshotRepository: BackupSnapshotRepository
) : EvictDeletedItemsFromSnapshot {

    override suspend fun invoke(deletedKeys: List<BackupItemKey>) {
        if (deletedKeys.isEmpty()) return

        evictAddresses(deletedKeys)
        evictContacts(deletedKeys)
    }

    private suspend fun evictAddresses(deletedKeys: List<BackupItemKey>) {
        val deletedAddresses = deletedKeys
            .filter { it.isAddress() }
            .map { it.value.removePrefix(BackupItemKey.ACCOUNTS_PREFIX) }
        if (deletedAddresses.isEmpty()) return

        backupSnapshotRepository.removeAddresses(deletedAddresses)
    }

    private suspend fun evictContacts(deletedKeys: List<BackupItemKey>) {
        val deletedContacts = deletedKeys
            .filter { it.isContact() }
            .map { it.value.removePrefix(BackupItemKey.CONTACTS_PREFIX) }
        if (deletedContacts.isEmpty()) return

        backupSnapshotRepository.removeContacts(deletedContacts)
    }
}

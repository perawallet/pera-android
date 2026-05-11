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

package com.algorand.backup.contact.domain.usecase

import com.algorand.backup.contact.domain.model.ContactChangeEvent
import com.algorand.backup.domain.model.BackupItemChange
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.usecase.BackupItemObserver
import com.algorand.backup.domain.usecase.BackupSyncStateUpdater
import com.algorand.backup.domain.usecase.HasBackup
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ContactBackupItemObserver @Inject constructor(
    private val getAllContactsFlow: GetAllContactsFlow,
    private val syncStateUpdater: BackupSyncStateUpdater,
    private val hasBackup: HasBackup,
    private val changeProcessor: BackupContactChangeProcessor
) : BackupItemObserver {

    override fun observeChanges(scope: CoroutineScope): Flow<BackupItemChange> {
        return getAllContactsFlow().map { contacts ->
            val event = changeProcessor.process(contacts)
            if (!hasBackup() || event !is ContactChangeEvent.Changed) {
                return@map BackupItemChange.NoChange
            }

            val deleteKeys = event.removedAddresses.map { BackupItemKey.contacts(it) }.toSet()
            if (deleteKeys.isNotEmpty()) {
                syncStateUpdater.markPendingDelete(deleteKeys)
            }

            val dirtyKeys = (event.addedAddresses + event.renamedAddresses)
                .map { BackupItemKey.contacts(it) }
                .toSet()
            if (dirtyKeys.isNotEmpty()) {
                syncStateUpdater.markDirty(dirtyKeys, BackupItemType.CONTACT)
            }

            BackupItemChange.SyncRequired
        }
    }

    override fun reset() {
        changeProcessor.reset()
    }
}

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

package com.algorand.android.ui.backup.list.usecase

import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.backup.contact.domain.usecase.ContactsBackupDataImporter
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.usecase.GetBackupId
import com.algorand.backup.domain.usecase.ReactivateBackupItem
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultAddBackupContactToLocal @Inject constructor(
    private val contactsBackupDataImporter: ContactsBackupDataImporter,
    private val reactivateBackupItem: ReactivateBackupItem,
    private val getBackupId: GetBackupId
) : AddBackupContactToLocal {

    override suspend fun invoke(payload: ContactBackupPayload): PeraResult<Unit> {
        val backupId = getBackupId() ?: return PeraResult.Error(IllegalStateException("No backup ID"))
        return try {
            contactsBackupDataImporter.importContacts(listOf(payload))
            reactivateBackupItem(backupId, BackupItemKey.contacts(payload.address))
            PeraResult.Success(Unit)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }
}

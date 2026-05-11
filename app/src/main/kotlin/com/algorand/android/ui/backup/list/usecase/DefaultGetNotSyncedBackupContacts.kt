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

import com.algorand.android.database.ContactDao
import com.algorand.android.ui.backup.list.model.BackupContactListItem
import com.algorand.backup.domain.usecase.GetContactBackupSnapshot
import javax.inject.Inject

internal class DefaultGetNotSyncedBackupContacts @Inject constructor(
    private val getContactBackupSnapshot: GetContactBackupSnapshot,
    private val contactDao: ContactDao
) : GetNotSyncedBackupContacts {

    override suspend fun invoke(): List<BackupContactListItem> {
        val localAddresses = contactDao.getAll().map { it.publicKey }.toHashSet()
        return getContactBackupSnapshot()
            .filter { it.address !in localAddresses }
            .map { payload ->
                BackupContactListItem(
                    name = payload.name,
                    address = payload.address,
                    payload = payload
                )
            }
    }
}

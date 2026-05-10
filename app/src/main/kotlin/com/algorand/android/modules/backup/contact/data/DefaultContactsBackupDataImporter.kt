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

package com.algorand.android.modules.backup.contact.data

import com.algorand.android.database.ContactDao
import com.algorand.android.models.User
import com.algorand.backup.contact.domain.model.ContactBackupPayload
import com.algorand.backup.contact.domain.usecase.ContactsBackupDataImporter
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class DefaultContactsBackupDataImporter @Inject constructor(
    private val contactDao: ContactDao,
    private val errorLogger: PeraErrorLogger
) : ContactsBackupDataImporter {

    override suspend fun importContacts(payloads: List<ContactBackupPayload>) {
        for (payload in payloads) {
            try {
                val existing = contactDao.getContactByAddress(payload.address)
                if (existing == null) {
                    contactDao.addContact(User(name = payload.name, publicKey = payload.address, imageUriAsString = null))
                } else {
                    contactDao.updateContact(existing.copy(name = payload.name))
                }
            } catch (e: Exception) {
                errorLogger.logError(e)
            }
        }
    }
}

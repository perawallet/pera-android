/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.repository

import com.algorand.android.database.ContactDao
import com.algorand.android.models.User
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {

    suspend fun getAllContacts(): List<User> {
        return contactDao.getAll()
    }

    suspend fun deleteAllContacts() {
        contactDao.deleteAllContacts()
    }

    suspend fun getContactByAddress(accountAddress: String): User? {
        return contactDao.getContactByAddress(accountAddress)
    }

    suspend fun addContact(contact: User) {
        contactDao.addContact(contact)
    }

    suspend fun updateContact(contact: User) {
        contactDao.updateContact(contact)
    }
}

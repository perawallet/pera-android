package com.algorand.android.module.contacts.domain.repository

import com.algorand.android.module.contacts.domain.model.Contact

internal interface ContactRepository {
    suspend fun getContactByAddress(address: String): Contact?
    suspend fun getAllContacts(): List<Contact>
    suspend fun deleteAllContacts()
    suspend fun getUsersWithNameFiltered(query: String): List<Contact>
    suspend fun updateContact(contact: Contact)
    suspend fun saveContact(contact: Contact)
    suspend fun deleteContact(address: String)
}

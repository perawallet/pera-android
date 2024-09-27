package com.algorand.android.module.contacts.data.repository

import com.algorand.android.module.contacts.data.mapper.ContactEntityMapper
import com.algorand.android.module.contacts.data.mapper.ContactMapper
import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.module.contacts.domain.repository.ContactRepository
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.shareddb.contact.dao.ContactDao

internal class ContactRepositoryImpl(
    private val contactDao: ContactDao,
    private val contactMapper: ContactMapper,
    private val contactEntityMapper: ContactEntityMapper,
    private val encryptionManager: EncryptionManager
) : ContactRepository {

    override suspend fun getContactByAddress(address: String): Contact? {
        val encryptedAddress = encryptionManager.encrypt(address)
        return contactDao.getContactByAddress(encryptedAddress)?.let {
            contactMapper(it)
        }
    }

    override suspend fun getAllContacts(): List<Contact> {
        return contactDao.getAll().map {
            contactMapper(it)
        }
    }

    override suspend fun deleteAllContacts() {
        contactDao.deleteAllContacts()
    }

    override suspend fun getUsersWithNameFiltered(query: String): List<Contact> {
        return contactDao.getUsersWithNameFiltered(query).map {
            contactMapper(it)
        }
    }

    override suspend fun saveContact(contact: Contact) {
        val contactEntity = contactEntityMapper(contact)
        contactDao.insertContact(contactEntity)
    }

    override suspend fun updateContact(contact: Contact) {
        val contactEntity = contactEntityMapper(contact)
        contactDao.insertContact(contactEntity)
    }

    override suspend fun deleteContact(address: String) {
        val encryptedAddress = encryptionManager.encrypt(address)
        contactDao.deleteContact(encryptedAddress)
    }
}

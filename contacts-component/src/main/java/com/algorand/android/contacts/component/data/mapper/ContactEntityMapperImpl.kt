package com.algorand.android.contacts.component.data.mapper

import com.algorand.android.contacts.component.domain.model.Contact
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.contact.model.ContactEntity

internal class ContactEntityMapperImpl(
    private val encryptionManager: EncryptionManager
) : ContactEntityMapper {

    override fun invoke(contact: Contact): ContactEntity {
        return ContactEntity(
            encryptedAddress = encryptionManager.encrypt(contact.address),
            name = contact.name,
            imageUri = contact.imageUri
        )
    }
}

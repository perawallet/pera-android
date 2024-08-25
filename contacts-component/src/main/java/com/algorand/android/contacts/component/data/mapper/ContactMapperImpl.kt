package com.algorand.android.contacts.component.data.mapper

import com.algorand.android.contacts.component.domain.model.Contact
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.contact.model.ContactEntity

internal class ContactMapperImpl(
    private val encryptionManager: EncryptionManager
) : ContactMapper {

    override fun invoke(entity: ContactEntity): Contact {
        return Contact(
            address = encryptionManager.decrypt(entity.encryptedAddress),
            name = entity.name,
            imageUri = entity.imageUri
        )
    }
}

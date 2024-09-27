package com.algorand.android.module.contacts.data.mapper

import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.shareddb.contact.model.ContactEntity

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

package com.algorand.android.module.contacts.data.mapper

import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.shared_db.contact.model.ContactEntity

internal interface ContactMapper {
    operator fun invoke(entity: ContactEntity): Contact
}

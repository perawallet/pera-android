package com.algorand.android.contacts.component.data.mapper

import com.algorand.android.contacts.component.domain.model.Contact
import com.algorand.android.shared_db.contact.model.ContactEntity

internal interface ContactMapper {
    operator fun invoke(entity: ContactEntity): Contact
}

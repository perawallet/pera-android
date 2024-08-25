package com.algorand.android.contacts.component.data.mapper

import com.algorand.android.contacts.component.domain.model.Contact
import com.algorand.android.shared_db.contact.model.ContactEntity

internal interface ContactEntityMapper {
    operator fun invoke(contact: Contact): ContactEntity
}

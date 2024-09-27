package com.algorand.android.module.contacts.data.mapper

import com.algorand.android.module.contacts.domain.model.Contact
import com.algorand.android.module.shareddb.contact.model.ContactEntity

internal interface ContactEntityMapper {
    operator fun invoke(contact: Contact): ContactEntity
}

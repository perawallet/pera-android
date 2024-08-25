package com.algorand.android.contacts.component.domain.usecase

import com.algorand.android.contacts.component.domain.model.Contact

fun interface GetAllContacts {
    suspend operator fun invoke(): List<Contact>
}

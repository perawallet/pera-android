package com.algorand.android.module.contacts.domain.usecase

import com.algorand.android.module.contacts.domain.model.Contact

fun interface GetAllContacts {
    suspend operator fun invoke(): List<Contact>
}

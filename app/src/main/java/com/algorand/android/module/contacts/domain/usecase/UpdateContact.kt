package com.algorand.android.module.contacts.domain.usecase

import com.algorand.android.module.contacts.domain.model.Contact

fun interface UpdateContact {
    suspend operator fun invoke(contact: Contact)
}

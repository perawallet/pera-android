package com.algorand.android.contacts.component.domain.usecase

import com.algorand.android.contacts.component.domain.model.Contact

fun interface UpdateContact {
    suspend operator fun invoke(contact: Contact)
}

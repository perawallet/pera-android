package com.algorand.android.contacts.component.domain.usecase

import com.algorand.android.contacts.component.domain.model.Contact

fun interface GetContactByAddress {
    suspend operator fun invoke(address: String): Contact?
}

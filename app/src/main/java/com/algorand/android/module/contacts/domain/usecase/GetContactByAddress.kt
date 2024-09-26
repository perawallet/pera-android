package com.algorand.android.module.contacts.domain.usecase

import com.algorand.android.module.contacts.domain.model.Contact

fun interface GetContactByAddress {
    suspend operator fun invoke(address: String): Contact?
}

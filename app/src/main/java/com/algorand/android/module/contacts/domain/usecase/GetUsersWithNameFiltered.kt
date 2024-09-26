package com.algorand.android.module.contacts.domain.usecase

import com.algorand.android.module.contacts.domain.model.Contact

fun interface GetUsersWithNameFiltered {
    suspend operator fun invoke(query: String): List<Contact>
}

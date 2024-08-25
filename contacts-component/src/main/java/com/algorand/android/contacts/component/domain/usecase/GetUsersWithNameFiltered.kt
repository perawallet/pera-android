package com.algorand.android.contacts.component.domain.usecase

import com.algorand.android.contacts.component.domain.model.Contact

fun interface GetUsersWithNameFiltered {
    suspend operator fun invoke(query: String): List<Contact>
}

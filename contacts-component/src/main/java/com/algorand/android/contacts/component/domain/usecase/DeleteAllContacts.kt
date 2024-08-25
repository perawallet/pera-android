package com.algorand.android.contacts.component.domain.usecase

fun interface DeleteAllContacts {
    suspend operator fun invoke()
}

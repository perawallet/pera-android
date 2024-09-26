package com.algorand.android.module.contacts.domain.usecase

fun interface DeleteAllContacts {
    suspend operator fun invoke()
}

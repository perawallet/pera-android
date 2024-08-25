package com.algorand.android.contacts.component.domain.usecase

fun interface DeleteContact {
    suspend operator fun invoke(address: String)
}

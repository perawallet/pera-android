package com.algorand.android.module.contacts.domain.usecase

fun interface DeleteContact {
    suspend operator fun invoke(address: String)
}

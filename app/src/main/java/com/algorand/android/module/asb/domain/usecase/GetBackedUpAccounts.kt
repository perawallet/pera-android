package com.algorand.android.module.asb.domain.usecase

fun interface GetBackedUpAccounts {
    suspend operator fun invoke(): Set<String>
}

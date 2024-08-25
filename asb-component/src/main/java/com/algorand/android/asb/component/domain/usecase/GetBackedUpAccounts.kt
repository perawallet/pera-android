package com.algorand.android.asb.component.domain.usecase

fun interface GetBackedUpAccounts {
    suspend operator fun invoke(): Set<String>
}

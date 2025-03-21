package com.algorand.wallet.encryption.domain.usecase

fun interface GetStrongBoxUsedCheck {
    suspend operator fun invoke(): Boolean
}

fun interface SaveStrongBoxUsedCheck {
    suspend operator fun invoke(check: Boolean)
}

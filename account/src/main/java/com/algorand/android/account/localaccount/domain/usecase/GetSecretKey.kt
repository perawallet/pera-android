package com.algorand.android.account.localaccount.domain.usecase

interface GetSecretKey {
    suspend operator fun invoke(address: String): ByteArray?
}

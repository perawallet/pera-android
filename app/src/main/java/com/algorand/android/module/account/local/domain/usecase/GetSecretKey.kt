package com.algorand.android.module.account.local.domain.usecase

interface GetSecretKey {
    suspend operator fun invoke(address: String): ByteArray?
}

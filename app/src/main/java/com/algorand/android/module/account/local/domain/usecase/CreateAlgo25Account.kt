package com.algorand.android.module.account.local.domain.usecase

interface CreateAlgo25Account {
    suspend operator fun invoke(address: String, secretKey: ByteArray)
}

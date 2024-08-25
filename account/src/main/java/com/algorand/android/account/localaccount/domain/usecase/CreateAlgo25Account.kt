package com.algorand.android.account.localaccount.domain.usecase

interface CreateAlgo25Account {
    suspend operator fun invoke(address: String, secretKey: ByteArray)
}

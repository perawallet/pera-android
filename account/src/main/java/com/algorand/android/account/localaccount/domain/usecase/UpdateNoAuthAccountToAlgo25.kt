package com.algorand.android.account.localaccount.domain.usecase

interface UpdateNoAuthAccountToAlgo25 {
    suspend operator fun invoke(address: String, secretKey: ByteArray)
}

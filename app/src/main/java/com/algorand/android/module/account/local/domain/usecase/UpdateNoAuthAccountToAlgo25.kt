package com.algorand.android.module.account.local.domain.usecase

interface UpdateNoAuthAccountToAlgo25 {
    suspend operator fun invoke(address: String, secretKey: ByteArray)
}

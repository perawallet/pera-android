package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.usecase.*
import javax.inject.Inject

internal class UpdateNoAuthAccountToAlgo25UseCase @Inject constructor(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val createAlgo25Account: CreateAlgo25Account
) : UpdateNoAuthAccountToAlgo25 {

    override suspend fun invoke(address: String, secretKey: ByteArray) {
        deleteLocalAccount(address)
        createAlgo25Account(address, secretKey)
    }
}

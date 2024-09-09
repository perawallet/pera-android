package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.*
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

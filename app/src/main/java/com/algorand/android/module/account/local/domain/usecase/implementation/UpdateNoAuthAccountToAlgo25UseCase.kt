package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.usecase.CreateAlgo25Account
import com.algorand.android.module.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.android.module.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25
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

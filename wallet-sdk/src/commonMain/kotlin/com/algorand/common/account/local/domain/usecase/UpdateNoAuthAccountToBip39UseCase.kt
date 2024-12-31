package com.algorand.common.account.local.domain.usecase

import com.algorand.common.account.local.domain.model.LocalAccount

internal class UpdateNoAuthAccountToBip39UseCase(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val saveBip39Account: SaveBip39Account
) : UpdateNoAuthAccountToBip39 {

    override suspend fun invoke(address: String, secretKey: ByteArray) {
        deleteLocalAccount(address)
        saveBip39Account(LocalAccount.Bip39(address, secretKey))
    }
}
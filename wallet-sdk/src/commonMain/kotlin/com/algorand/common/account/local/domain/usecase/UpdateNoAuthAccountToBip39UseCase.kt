package com.algorand.common.account.local.domain.usecase

internal class UpdateNoAuthAccountToBip39UseCase(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val createBip39Account: CreateBip39Account
) : UpdateNoAuthAccountToBip39 {

    override suspend fun invoke(address: String, secretKey: ByteArray) {
        deleteLocalAccount(address)
        createBip39Account(address, secretKey)
    }
}
package com.algorand.common.account.local.domain.usecase

import com.algorand.common.algosdk.Bip32DerivationType

internal class UpdateNoAuthAccountToHdKeyUseCase(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val createHdKeyAccount: CreateHdKeyAccount
) : UpdateNoAuthAccountToHdKey {

    override suspend fun invoke(
        address: String,
        publicKey: ByteArray,
        privateKey: ByteArray,
        seedId: Int,
        account: Int,
        change: Int,
        keyIndex: Int,
        derivationType: Bip32DerivationType
    ) {
        deleteLocalAccount(address)
        createHdKeyAccount.invoke(
            address,
            publicKey,
            privateKey,
            seedId,
            account,
            change,
            keyIndex,
            derivationType
        )
    }
}
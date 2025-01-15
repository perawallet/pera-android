package com.algorand.common.account.local.domain.usecase

import com.algorand.common.algosdk.Bip32DerivationType
import com.algorand.common.encryption.SecretKeyEncryptionManager

internal class UpdateNoAuthAccountToHdKeyUseCase(
    private val deleteLocalAccount: DeleteLocalAccount,
    private val createHdKeyAccount: CreateHdKeyAccount,
    private val secretKeyEncryptionManager: SecretKeyEncryptionManager
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
            secretKeyEncryptionManager.encrypt(privateKey),
            seedId,
            account,
            change,
            keyIndex,
            derivationType
        )
    }
}
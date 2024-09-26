package com.algorand.android.module.asb.domain.usecase.implementation

import com.algorand.android.module.algosdk.AlgoSdkBackUp
import com.algorand.android.module.asb.domain.usecase.CreateAsbCipherKey
import javax.inject.Inject

internal class CreateAsbCipherKeyUseCase @Inject constructor(
    private val algoSdkBackUp: AlgoSdkBackUp
) : CreateAsbCipherKey {

    override fun invoke(mnemonics: String): ByteArray? {
        val privateKey = algoSdkBackUp.derivePrivateKeyFromMnemonics(mnemonics) ?: return null
        return algoSdkBackUp.generateBackupCipherKey(CIPHER_KEY, privateKey)
    }

    private companion object {
        const val CIPHER_KEY = "Algorand export 1.0"
    }
}

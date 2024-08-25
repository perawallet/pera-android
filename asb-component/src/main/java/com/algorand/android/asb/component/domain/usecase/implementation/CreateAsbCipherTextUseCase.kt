package com.algorand.android.asb.component.domain.usecase.implementation

import com.algorand.android.algosdk.component.*
import com.algorand.android.asb.component.domain.usecase.*
import javax.inject.Inject

internal class CreateAsbCipherTextUseCase @Inject constructor(
    private val createAsbCipherKey: CreateAsbCipherKey,
    private val algoSdkEncryption: AlgoSdkEncryption
) : CreateAsbCipherText {

    override fun invoke(payload: String, mnemonics: List<String>): String? {
        val cipherKey = createAsbCipherKey(mnemonics.joinMnemonics()) ?: return null
        return algoSdkEncryption.encryptContent(payload.toByteArray(), cipherKey)
    }
}

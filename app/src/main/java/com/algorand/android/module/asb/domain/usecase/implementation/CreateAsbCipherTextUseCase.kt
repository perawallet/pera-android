package com.algorand.android.module.asb.domain.usecase.implementation

import com.algorand.android.algosdk.component.AlgoSdkEncryption
import com.algorand.android.algosdk.component.joinMnemonics
import com.algorand.android.module.asb.domain.usecase.CreateAsbCipherKey
import com.algorand.android.module.asb.domain.usecase.CreateAsbCipherText
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

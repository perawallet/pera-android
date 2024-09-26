package com.algorand.android.module.asb.domain.usecase.implementation

import com.algorand.android.algosdk.component.AlgoSdkBackUp
import com.algorand.android.module.asb.domain.usecase.CreateAsbMnemonic
import javax.inject.Inject

internal class CreateAsbMnemonicUseCase @Inject constructor(
    private val algoSdkBackUp: AlgoSdkBackUp
) : CreateAsbMnemonic {

    override fun invoke(): String? {
        val privateKey = algoSdkBackUp.generateBackupPrivateKey() ?: return null
        val mnemonics = algoSdkBackUp.generateMnemonicsFromBackupKey(privateKey)
        return mnemonics.takeIf { !it.isNullOrBlank() }
    }
}

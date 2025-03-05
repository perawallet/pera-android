package com.algorand.wallet.algosdk.transaction.sdk

interface Bip39MnemonicGenerator {
    fun getSeedFromEntropy(entropy: ByteArray): ByteArray?
    fun getMnemonicFromEntropy(entropy: ByteArray): String?
}
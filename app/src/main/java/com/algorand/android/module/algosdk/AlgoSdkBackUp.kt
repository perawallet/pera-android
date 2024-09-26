package com.algorand.android.module.algosdk

interface AlgoSdkBackUp {
    fun generateBackupPrivateKey(): ByteArray?
    fun generateBackupCipherKey(key: String, input: ByteArray): ByteArray?
    fun generateMnemonicsFromBackupKey(backupKey: ByteArray): String?
    fun derivePrivateKeyFromMnemonics(mnemonics: String): ByteArray?
}

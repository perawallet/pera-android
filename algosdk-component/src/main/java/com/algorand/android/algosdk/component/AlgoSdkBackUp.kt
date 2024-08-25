package com.algorand.android.algosdk.component

interface AlgoSdkBackUp {
    fun generateBackupPrivateKey(): ByteArray?
    fun generateBackupCipherKey(key: String, input: ByteArray): ByteArray?
    fun generateMnemonicsFromBackupKey(backupKey: ByteArray): String?
    fun derivePrivateKeyFromMnemonics(mnemonics: String): ByteArray?
}

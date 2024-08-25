package com.algorand.android.algosdk.component

import com.algorand.algosdk.sdk.Sdk
import javax.inject.Inject

internal class AlgoSdkBackUpImpl @Inject constructor() : AlgoSdkBackUp {

    override fun generateBackupPrivateKey(): ByteArray? {
        return try {
            Sdk.generateBackupPrivateKey()
        } catch (exception: Exception) {
            null
        }
    }

    override fun generateBackupCipherKey(key: String, input: ByteArray): ByteArray? {
        return try {
            Sdk.generateBackupCipherKey(key, input)
        } catch (exception: Exception) {
            null
        }
    }

    override fun generateMnemonicsFromBackupKey(backupKey: ByteArray): String? {
        return try {
            Sdk.backupMnemonicFromKey(backupKey)
        } catch (exception: Exception) {
            null
        }
    }

    override fun derivePrivateKeyFromMnemonics(mnemonics: String): ByteArray? {
        return try {
            Sdk.backupMnemonicToKey(mnemonics)
        } catch (exception: Exception) {
            null
        }
    }
}

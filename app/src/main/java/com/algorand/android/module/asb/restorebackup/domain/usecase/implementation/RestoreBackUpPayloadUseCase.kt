package com.algorand.android.module.asb.restorebackup.domain.usecase.implementation

import android.util.Base64
import com.algorand.android.module.algosdk.AlgoSdkEncryption
import com.algorand.android.module.asb.backupprotocol.model.BackUpAccount
import com.algorand.android.module.asb.backupprotocol.model.BackUpProtocolElement
import com.algorand.android.module.asb.backupprotocol.model.BackUpProtocolPayload
import com.algorand.android.module.asb.domain.usecase.CreateAsbCipherKey
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult.Success
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult.UnableToCreateCipherKey
import com.algorand.android.module.asb.restorebackup.domain.model.RestoreBackUpPayloadResult.UnableToRestoreAccounts
import com.algorand.android.module.asb.restorebackup.domain.usecase.RestoreBackUpPayload
import com.algorand.android.module.foundation.json.JsonSerializer
import javax.inject.Inject

internal class RestoreBackUpPayloadUseCase @Inject constructor(
    private val createAsbCipherKey: CreateAsbCipherKey,
    private val algoSdkEncryption: AlgoSdkEncryption,
    private val jsonSerializer: JsonSerializer
) : RestoreBackUpPayload {

    override fun invoke(keyInput: String, cipherText: String): RestoreBackUpPayloadResult {
        val cipherKey = createAsbCipherKey(keyInput) ?: return UnableToCreateCipherKey
        val decryptedContent = getDecryptedContent(cipherKey, cipherText) ?: return UnableToRestoreAccounts
        if (decryptedContent.accounts.isNullOrEmpty()) return UnableToRestoreAccounts
        val restoredAccounts = getBackUpAccountList(decryptedContent.accounts)
        return Success(restoredAccounts)
    }

    private fun getBackUpAccountList(restoredAccounts: List<BackUpProtocolElement>): List<BackUpAccount> {
        return restoredAccounts.mapNotNull {
            BackUpAccount(
                address = it.address ?: return@mapNotNull null,
                name = it.name.orEmpty(),
                secretKey = it.privateKey?.decodeBase64ToByteArray() ?: return@mapNotNull null
            )
        }
    }

    private fun String.decodeBase64ToByteArray(): ByteArray? {
        return try {
            Base64.decode(this, Base64.NO_WRAP)
        } catch (exception: Exception) {
            null
        }
    }

    private fun getDecryptedContent(cipherKey: ByteArray, cipherText: String): BackUpProtocolPayload? {
        val decryptedContent = algoSdkEncryption.decryptContent(cipherText, cipherKey)
        return decryptedContent?.let {
            jsonSerializer.fromJson(it, BackUpProtocolPayload::class.java)
        }
    }
}

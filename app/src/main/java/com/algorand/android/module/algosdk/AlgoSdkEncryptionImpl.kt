package com.algorand.android.module.algosdk

import android.util.Base64
import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.module.algosdk.AlgoSdkConstants.SDK_RESULT_SUCCESS
import javax.inject.Inject

internal class AlgoSdkEncryptionImpl @Inject constructor() : AlgoSdkEncryption {

    override fun encryptContent(content: ByteArray, key: ByteArray): String? {
        return try {
            val encryption = Sdk.encrypt(content, key)
            return if (encryption.errorCode == SDK_RESULT_SUCCESS) {
                encryption.encryptedData.encodeBase64()
            } else {
                null
            }
        } catch (exception: Exception) {
            null
        }
    }

    override fun decryptContent(encryptedContent: String, key: ByteArray): String? {
        return try {
            val decodedContent = encryptedContent.decodeBase64ToByteArray()
            val decryption = Sdk.decrypt(decodedContent, key)
            return if (decryption.errorCode == SDK_RESULT_SUCCESS) {
                String(decryption.decryptedData)
            } else {
                null
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun ByteArray.encodeBase64(): String? {
        return try {
            Base64.encodeToString(this, Base64.NO_WRAP)
        } catch (exception: Exception) {
            null
        }
    }

    private fun String.decodeBase64ToByteArray(): ByteArray? {
        return try {
            Base64.decode(this, Base64.NO_WRAP)
        } catch (exception: Exception) {
            null
        }
    }
}

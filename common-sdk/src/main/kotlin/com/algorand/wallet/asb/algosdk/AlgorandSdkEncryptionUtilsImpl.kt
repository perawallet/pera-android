/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asb.algosdk

import android.util.Base64.NO_WRAP
import com.algorand.algosdk.sdk.Sdk
import com.algorand.wallet.encryption.domain.manager.Base64Manager
import javax.inject.Inject

internal class AlgorandSdkEncryptionUtilsImpl @Inject constructor(
    private val base64Manager: Base64Manager
) : AlgorandSdkEncryptionUtils {

    override fun encryptContent(content: ByteArray, key: ByteArray): String? {
        return try {
            val encryption = Sdk.encrypt(content, key)
            return if (encryption.errorCode == SDK_RESULT_SUCCESS) {
                android.util.Base64.encodeToString(encryption.encryptedData, NO_WRAP)
            } else {
                null
            }
        } catch (exception: Exception) {
            null
        }
    }

    override fun decryptContent(encryptedContent: String, key: ByteArray): String? {
        return try {
            val decodedContent = android.util.Base64.decode(encryptedContent, NO_WRAP)
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

    private companion object {
        const val SDK_RESULT_SUCCESS = 0L
    }
}

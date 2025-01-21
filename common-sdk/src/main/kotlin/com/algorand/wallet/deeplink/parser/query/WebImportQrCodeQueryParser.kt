/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.deeplink.parser.query

import com.algorand.wallet.deeplink.model.PeraUri
import com.algorand.wallet.deeplink.model.WebImportQrCode
import com.algorand.wallet.foundation.json.JsonSerializer
import com.google.gson.annotations.SerializedName
import javax.inject.Inject

internal class WebImportQrCodeQueryParser @Inject constructor(
    private val jsonSerializer: JsonSerializer
) : DeepLinkQueryParser<WebImportQrCode?> {

    override fun parseQuery(peraUri: PeraUri): WebImportQrCode? {
        return try {
            getWebImportQrCode(peraUri)
        } catch (e: Exception) {
            null
        }
    }

    private fun getWebImportQrCode(peraUri: PeraUri): WebImportQrCode? {
        val qrCodePayload = jsonSerializer.fromJson(peraUri.rawUri, WebQrCode::class.java) ?: return null
        return if (isRecognized(qrCodePayload) && qrCodePayload.action == ACTION_IMPORT_KEY) {
            WebImportQrCode(qrCodePayload.backupId, qrCodePayload.encryptionKey)
        } else {
            null
        }
    }

    private fun isRecognized(webQrCode: WebQrCode): Boolean {
        val intVersion = webQrCode.version.toIntOrNull() ?: return false
        return intVersion <= CURRENT_QR_CODE_VERSION
    }

    internal data class WebQrCode(
        @SerializedName("version") val version: String,
        @SerializedName("action") val action: String,
        @SerializedName("platform") val platform: String?,
        @SerializedName("backupId") val backupId: String,
        @SerializedName("modificationKey") val modificationKey: String?,
        @SerializedName("encryptionKey") val encryptionKey: String,
    )

    private companion object {
        const val CURRENT_QR_CODE_VERSION = 1
        const val ACTION_IMPORT_KEY = "import"
    }
}

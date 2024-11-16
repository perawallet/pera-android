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

package com.algorand.common.deeplink.parser.query

import com.algorand.common.deeplink.model.PeraUri
import com.algorand.common.deeplink.model.WebImportQrCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class WebImportQrCodeQueryParser : DeepLinkQueryParser<WebImportQrCode?> {

    override fun parseQuery(peraUri: PeraUri): WebImportQrCode? {
        return try {
            getWebImportQrCode(peraUri)
        } catch (e: Exception) {
            null
        }
    }

    private fun getWebImportQrCode(peraUri: PeraUri): WebImportQrCode? {
        val qrCodePayload = Json.decodeFromString<WebQrCode>(peraUri.rawUri)
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

    @Serializable
    private data class WebQrCode(
        @SerialName("version") val version: String,
        @SerialName("action") val action: String,
        @SerialName("platform") val platform: String?,
        @SerialName("backupId") val backupId: String,
        @SerialName("modificationKey") val modificationKey: String?,
        @SerialName("encryptionKey") val encryptionKey: String,
    )

    private companion object {
        const val CURRENT_QR_CODE_VERSION = 1
        const val ACTION_IMPORT_KEY = "import"
    }
}

/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.deeplink.mapper

import com.algorand.android.deeplink.model.WebImportQrCode
import com.algorand.android.deeplink.model.WebQrCode
import javax.inject.Inject

internal class WebImportQrCodeMapperImpl @Inject constructor() : WebImportQrCodeMapper {

    override fun invoke(qrCode: WebQrCode): WebImportQrCode? {
        return if (isRecognized(qrCode) && qrCode.action == ACTION_IMPORT_KEY) {
            WebImportQrCode(
                backupId = qrCode.backupId,
                encryptionKey = qrCode.encryptionKey,
            )
        } else {
            null
        }
    }

    private fun isRecognized(webQrCode: WebQrCode): Boolean {
        val intVersion = webQrCode.version.toIntOrNull() ?: return false
        return intVersion <= CURRENT_QR_CODE_VERSION
    }

    private companion object {
        const val CURRENT_QR_CODE_VERSION = 1
        const val ACTION_IMPORT_KEY = "import"
    }
}

/*
fun mapFromWebQrCode(
        webQrCode: WebQrCode,
    ): WebImportQrCode? {
        return if (isRecognized(webQrCode) && webQrCode.action == WebQrCode.ACTION_IMPORT_KEY) {
            WebImportQrCode(
                backupId = webQrCode.backupId,
                encryptionKey = webQrCode.encryptionKey,
            )
        } else {
            null
        }
    }

    private fun isRecognized(webQrCode: WebQrCode): Boolean {
        val intVersion = webQrCode.version.toIntOrNull() ?: return false
        return intVersion <= WebQrCode.CURRENT_QR_CODE_VERSION
    }
 */
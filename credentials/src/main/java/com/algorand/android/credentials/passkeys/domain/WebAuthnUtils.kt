/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.credentials.passkeys.domain

import android.util.Base64
import androidx.credentials.provider.CallingAppInfo
import java.security.MessageDigest

/**
 * This class is a duplicated version of the original [androidx.credentials.webauthn.WebAuthnUtils]
 * from the WebAuthn library, which is restricted to library-only usage.
 *
 * It was duplicated intentionally to simplify integration and avoid the need to create
 * multiple sub-models and data mappers that would otherwise be required to work around
 * its restricted visibility.
 *
 * The functionality and structure are preserved as-is for compatibility and maintainability.
 */
internal class WebAuthnUtils {

    companion object {
        fun b64Decode(str: String): ByteArray {
            return Base64.decode(str, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
        }

        fun b64Encode(data: ByteArray): String {
            return Base64.encodeToString(
                data,
                Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE
            )
        }

        fun appInfoToOrigin(info: CallingAppInfo): String {
            val cert = info.signingInfo.apkContentsSigners[0].toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val certHash = md.digest(cert)
            return "android:apk-key-hash:${b64Encode(certHash)}"
        }
    }
}

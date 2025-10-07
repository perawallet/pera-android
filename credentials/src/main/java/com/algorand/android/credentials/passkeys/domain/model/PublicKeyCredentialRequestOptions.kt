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

package com.algorand.android.credentials.passkeys.domain.model

import com.algorand.android.credentials.passkeys.domain.WebAuthnUtils
import org.json.JSONObject

/**
 * This class is a duplicated version of the original [androidx.credentials.webauthn.PublicKeyCredentialRequestOptions]
 * from the WebAuthn library, which is restricted to library-only usage.
 *
 * It was duplicated intentionally to simplify integration and avoid the need to create
 * multiple sub-models and data mappers that would otherwise be required to work around
 * its restricted visibility.
 *
 * The functionality and structure are preserved as-is for compatibility and maintainability.
 */
internal class PublicKeyCredentialRequestOptions(requestJson: String) {
    private val json: JSONObject = JSONObject(requestJson)

    val challenge: ByteArray
    private val timeout: Long

    val rpId: String
    private val userVerification: String

    init {
        val challengeString = json.getString("challenge")
        challenge = WebAuthnUtils.b64Decode(challengeString)
        timeout = json.optLong("timeout", 0)
        rpId = json.optString("rpId", "")
        userVerification = json.optString("userVerification", "preferred")
    }
}

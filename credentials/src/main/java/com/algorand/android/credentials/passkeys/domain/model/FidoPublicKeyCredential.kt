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
 * This class is a duplicated version of the original [androidx.credentials.webauthn.FidoPublicKeyCredential]
 * from the WebAuthn library, which is restricted to library-only usage.
 *
 * It was duplicated intentionally to simplify integration and avoid the need to create
 * multiple sub-models and data mappers that would otherwise be required to work around
 * its restricted visibility.
 *
 * The functionality and structure are preserved as-is for compatibility and maintainability.
 */
internal class FidoPublicKeyCredential(
    val encodedId: String,
    val response: AuthenticatorResponse,
    val authenticatorAttachment: String = DEFAULT_AUTH_ATTACHMENT
) {

    constructor(
        rawId: ByteArray,
        response: AuthenticatorResponse,
        authenticatorAttachment: String = DEFAULT_AUTH_ATTACHMENT
    ) : this(WebAuthnUtils.b64Encode(rawId), response, authenticatorAttachment)

    fun json(): String {
        val ret = JSONObject()
        ret.put("id", encodedId)
        ret.put("rawId", encodedId)
        ret.put("type", "public-key")
        ret.put("authenticatorAttachment", authenticatorAttachment)
        ret.put("response", response.json())
        ret.put("clientExtensionResults", extensionJson())
        return ret.toString()
    }

    private fun extensionJson(): JSONObject {
        val json = JSONObject()
        json.put("credProps", credPropsJson())
        return json
    }

    private fun credPropsJson(): JSONObject {
        val response = JSONObject()
        response.put("rk", true)
        return response
    }

    private companion object {
        const val DEFAULT_AUTH_ATTACHMENT = "platform"
    }
}

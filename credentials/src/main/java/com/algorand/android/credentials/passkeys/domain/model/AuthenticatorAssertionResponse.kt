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

import com.algorand.android.credentials.passkeys.domain.PeraMessageDigest
import com.algorand.android.credentials.passkeys.domain.WebAuthnUtils
import org.json.JSONObject

/**
 * This class is a duplicated version of the original [androidx.credentials.webauthn.AuthenticatorAssertionResponse]
 * from the WebAuthn library, which is restricted to library-only usage.
 *
 * It was duplicated intentionally to simplify integration and avoid the need to create
 * multiple sub-models and data mappers that would otherwise be required to work around
 * its restricted visibility.
 *
 * The functionality and structure are preserved as-is for compatibility and maintainability.
 */
internal class AuthenticatorAssertionResponse(
    private val requestOptions: PublicKeyCredentialRequestOptions,
    origin: String,
    private val authFlags: AuthenticatorFlags,
    private var userHandle: String,
    packageName: String? = null,
    private val clientDataHash: ByteArray? = null,
) : AuthenticatorResponse {
    override var clientJson = JSONObject()
    private var authenticatorData: ByteArray
    var signature: ByteArray = byteArrayOf()

    init {
        clientJson.put("type", "webauthn.get")
        clientJson.put("challenge", WebAuthnUtils.b64Encode(requestOptions.challenge))
        clientJson.put("origin", origin)
        if (packageName != null) {
            clientJson.put("androidPackageName", packageName)
        }

        authenticatorData = defaultAuthenticatorData()
    }

    @Suppress("MagicNumber")
    private fun defaultAuthenticatorData(): ByteArray {
        val md = PeraMessageDigest.getInstance()
        val rpHash = md.digest(requestOptions.rpId.toByteArray())
        var flags = 0
        if (authFlags.userPresent) {
            flags = flags or 0x01
        }
        if (authFlags.userVerified) {
            flags = flags or 0x04
        }
        if (authFlags.backupEligibility) {
            flags = flags or 0x08
        }
        if (authFlags.backupState) {
            flags = flags or 0x10
        }
        return rpHash +
                byteArrayOf(flags.toByte()) +
                byteArrayOf(0, 0, 0, 0)
    }

    fun dataToSign(): ByteArray {
        val md = PeraMessageDigest.getInstance()
        val hash: ByteArray = clientDataHash ?: md.digest(clientJson.toString().toByteArray())

        return authenticatorData + hash
    }

    override fun json(): JSONObject {
        val clientData = clientJson.toString().toByteArray()
        val response = JSONObject()
        if (clientDataHash == null) {
            response.put("clientDataJSON", WebAuthnUtils.b64Encode(clientData))
        }
        response.put("authenticatorData", WebAuthnUtils.b64Encode(authenticatorData))
        response.put("signature", WebAuthnUtils.b64Encode(signature))
        response.put("userHandle", userHandle)
        return response
    }
}

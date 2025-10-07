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

import android.util.Log
import com.algorand.android.credentials.passkeys.domain.PeraMessageDigest
import com.algorand.android.credentials.passkeys.domain.WebAuthnUtils
import com.algorand.android.credentials.passkeys.foundation.Cbor
import java.nio.ByteBuffer
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

/**
 * This class is a duplicated version of the original [androidx.credentials.webauthn.AuthenticatorAttestationResponse]
 * from the WebAuthn library, which is restricted to library-only usage.
 *
 * It was duplicated intentionally to simplify integration and avoid the need to create
 * multiple sub-models and data mappers that would otherwise be required to work around
 * its restricted visibility.
 *
 * The functionality and structure are preserved as-is for compatibility and maintainability.
 */
internal class AuthenticatorAttestationResponse(
    private val requestOptions: PublicKeyCredentialCreationOptions,
    private val credentialId: ByteArray,
    private val credentialPublicKey: ByteArray,
    origin: String,
    private val authFlags: AuthenticatorFlags,
    private val aaguid: UUID,
    packageName: String? = null,
    private val clientDataHash: ByteArray? = null,
    private val spki: ByteArray? = null,
) : AuthenticatorResponse {
    override var clientJson = JSONObject()
    private var attestationObject: ByteArray

    init {
        clientJson.put("type", "webauthn.create")
        clientJson.put("challenge", WebAuthnUtils.b64Encode(requestOptions.challenge))
        clientJson.put("origin", origin)
        if (packageName != null) {
            clientJson.put("androidPackageName", packageName)
        }

        attestationObject = defaultAttestationObject()
    }

    @Suppress("MagicNumber")
    private fun authData(): ByteArray {
        val md = PeraMessageDigest.getInstance()
        val rpHash = md.digest(requestOptions.rp.id.toByteArray())
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
        flags = flags or 0x40

        val aaguid = uuidToBytes(aaguid)
        val credIdLen = byteArrayOf((credentialId.size shr 8).toByte(), credentialId.size.toByte())

        return rpHash +
            byteArrayOf(flags.toByte()) +
            byteArrayOf(0, 0, 0, 0) +
            aaguid +
            credIdLen +
            credentialId +
            credentialPublicKey
    }

    @Suppress("MagicNumber")
    private fun uuidToBytes(uuid: UUID): ByteArray {
        val byteBuffer = ByteBuffer.wrap(ByteArray(16))
        byteBuffer.putLong(uuid.mostSignificantBits)
        byteBuffer.putLong(uuid.leastSignificantBits)
        return byteBuffer.array()
    }

    private fun addParsedAttestationObjectFieldsToJSON(
        authData: ByteArray,
        publicKeyAlgorithm: Long,
        jsonOutput: JSONObject,
    ) {
        // https://www.w3.org/TR/webauthn-2/#sctn-generating-an-attestation-object
        jsonOutput.put("authenticatorData", WebAuthnUtils.b64Encode(authData))
        jsonOutput.put("publicKeyAlgorithm", publicKeyAlgorithm)
        if (spki != null) {
            jsonOutput.put("publicKey", WebAuthnUtils.b64Encode(spki))
        } else {
            Log.i("AuthAttest", " Public key is null")
        }
    }

    private fun defaultAttestationObject(): ByteArray {
        val ao = mutableMapOf<String, Any>()
        ao["fmt"] = "none"
        ao["attStmt"] = emptyMap<Any, Any>()
        ao["authData"] = authData()
        return Cbor().encode(ao)
    }

    override fun json(): JSONObject {
        // See AuthenticatorAttestationResponseJSON at
        // https://w3c.github.io/webauthn/#ref-for-dom-publickeycredential-tojson

        val clientData = clientJson.toString().toByteArray()
        val response = JSONObject()
        if (clientDataHash == null) {
            response.put("clientDataJSON", WebAuthnUtils.b64Encode(clientData))
        }
        response.put("attestationObject", WebAuthnUtils.b64Encode(attestationObject))
        response.put("transports", JSONArray(listOf("internal", "hybrid")))

        addParsedAttestationObjectFieldsToJSON(
            authData(),
            getPublicKeyAlgorithm(),
            response,
        )

        return response
    }

    @Suppress("MagicNumber")
    private fun getPublicKeyAlgorithm(): Long {
        // Learn more here : https://www.iana.org/assignments/cose/cose.xhtml#algorithms
        return -7
    }
}

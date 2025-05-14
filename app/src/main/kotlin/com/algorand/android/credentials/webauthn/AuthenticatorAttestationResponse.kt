package com.algorand.android.credentials.webauthn

import android.util.Log
import com.algorand.android.credentials.encoding.b64Encode
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

class AuthenticatorAttestationResponse(
    private val requestOptions: PublicKeyCredentialCreationOptions,
    private val credentialId: ByteArray,
    private val credentialPublicKey: ByteArray,
    origin: String,
    private val authFlags: AuthenticatorFlags,
    packageName: String? = null,
    private val clientDataHash: ByteArray? = null,
    private val spki: ByteArray? = null,
) : AuthenticatorResponse {
    override var clientJson = JSONObject()
    private var attestationObject: ByteArray

    init {
        clientJson.put("type", "webauthn.create")
        clientJson.put("challenge", b64Encode(requestOptions.challenge))
        clientJson.put("origin", origin)
        if (packageName != null) {
            clientJson.put("androidPackageName", packageName)
        }

        attestationObject = defaultAttestationObject()
    }

    @Suppress("MagicNumber")
    private fun authData(): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        val rpHash = md.digest(requestOptions.rp.id.toByteArray())
        var flags = 0
        if (authFlags.up) {
            flags = flags or 0x01
        }
        if (authFlags.uv) {
            flags = flags or 0x04
        }
        if (authFlags.be) {
            flags = flags or 0x08
        }
        if (authFlags.bs) {
            flags = flags or 0x10
        }
        flags = flags or 0x40

        val aaguid = ByteArray(16) { 0 }
        val credIdLen = byteArrayOf((credentialId.size shr 8).toByte(), credentialId.size.toByte())

        return rpHash +
            byteArrayOf(flags.toByte()) +
            byteArrayOf(0, 0, 0, 0) +
            aaguid +
            credIdLen +
            credentialId +
            credentialPublicKey
    }

    private fun addParsedAttestationObjectFieldsToJSON(
        authData: ByteArray,
        publicKeyAlgorithm: Long,
        jsonOutput: JSONObject,
    ) {
        // https://www.w3.org/TR/webauthn-2/#sctn-generating-an-attestation-object
        jsonOutput.put(
            "authenticatorData",
            b64Encode(authData),
        )
        jsonOutput.put("publicKeyAlgorithm", publicKeyAlgorithm)
        if (spki != null) {
            jsonOutput.put("publicKey", b64Encode(spki))
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
            response.put("clientDataJSON", b64Encode(clientData))
        }
        response.put("attestationObject", b64Encode(attestationObject))
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

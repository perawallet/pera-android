package com.algorand.android.credentials.webauthn

import com.algorand.android.credentials.encoding.b64Encode
import org.json.JSONObject
import java.security.MessageDigest

class AuthenticatorAssertionResponse(
    private val requestOptions: PublicKeyCredentialRequestOptions,
    origin: String,
    private val authFlags: AuthenticatorFlags,
    private var userHandle: ByteArray,
    packageName: String? = null,
    private val clientDataHash: ByteArray? = null,
) : AuthenticatorResponse {
    override var clientJson: JSONObject = JSONObject()
    private var authenticatorData: ByteArray
    var signature: ByteArray = byteArrayOf()

    init {
        clientJson.put("type", "webauthn.get")
        clientJson.put("challenge", b64Encode(requestOptions.challenge))
        clientJson.put("origin", origin)
        if (packageName != null) {
            clientJson.put("androidPackageName", packageName)
        }

        authenticatorData = defaultAuthenticatorData()
    }

    @Suppress("MagicNumber")
    private fun defaultAuthenticatorData(): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        val rpHash = md.digest(requestOptions.rpId.toByteArray())
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
        return rpHash +
                byteArrayOf(flags.toByte()) +
                byteArrayOf(0, 0, 0, 0)
    }

    /**
     * Computes the data to sign.
     *
     * @return The data to sign.
     */
    fun dataToSign(): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        val hash: ByteArray = clientDataHash ?: md.digest(clientJson.toString().toByteArray())

        return authenticatorData + hash
    }

    /**
     * Converts the response to a JSON object.
     *
     * @return The JSON object representation of the response.
     */
    override fun json(): JSONObject {
        val clientData = clientJson.toString().toByteArray()
        val response = JSONObject()
        if (clientDataHash == null) {
            response.put("clientDataJSON", b64Encode(clientData))
        }
        response.put("authenticatorData", b64Encode(authenticatorData))
        response.put("signature", b64Encode(signature))
        response.put("userHandle", b64Encode(userHandle))
        return response
    }
}

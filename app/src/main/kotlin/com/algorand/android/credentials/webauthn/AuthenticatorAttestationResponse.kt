package com.algorand.android.credentials.webauthn

import android.util.Log
import com.algorand.android.credentials.encoding.b64Encode
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID

/**
 * Represents the response payload for the creation of a new public key credential during
 * WebAuthn registration. It provides the details of the authenticator's attestation response.
 *
 * This class implements the [AuthenticatorResponse] interface and includes functionality
 * to encode the attestation object and parse the client and authenticator data as specified
 * in the WebAuthn standard.
 *
 * @constructor Instantiates an [AuthenticatorAttestationResponse] with all necessary attributes.
 * @param requestOptions The public key credential creation options containing the
 * Relying Party's (RP) and user's information.
 * @param credentialId The unique identifier for the newly created credential.
 * @param credentialPublicKey The public key associated with the newly created credential.
 * @param origin The origin of the request that initiated the credential creation.
 * @param authFlags Flags indicating the authenticator's capabilities and state.
 * @param aaguid The Authenticator Attestation GUID (AAGUID) identifying the authenticator model.
 * @param packageName Optional package name of the Android application initiating the registration.
 * @param clientDataHash Optional SHA-256 hash of the client data used during the WebAuthn interaction.
 * @param spki Optional Subject Public Key Info (SPKI) for the credential.
 */
class AuthenticatorAttestationResponse(
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
    override var clientJson: JSONObject = JSONObject()
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

    /**
     * Adds parsed fields from an attestation object to a JSON object.
     *
     * @param authData The authenticator data in byte array form.
     * @param publicKeyAlgorithm The algorithm identifier for the associated public key.
     * @param jsonOutput The JSON object to which the parsed attestation fields will be added.
     */
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

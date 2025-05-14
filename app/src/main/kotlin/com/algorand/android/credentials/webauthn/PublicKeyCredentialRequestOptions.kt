package com.algorand.android.credentials.webauthn

import com.algorand.android.credentials.encoding.b64Decode
import org.json.JSONObject

class PublicKeyCredentialRequestOptions(requestJson: String) {
    private val json: JSONObject = JSONObject(requestJson)

    val challenge: ByteArray
    private val timeout: Long

    val rpId: String
    private val userVerification: String

    init {
        val challengeString = json.getString("challenge")
        challenge = b64Decode(challengeString)
        timeout = json.optLong("timeout", 0)
        rpId = json.optString("rpId", "")
        userVerification = json.optString("userVerification", "preferred")
    }
}

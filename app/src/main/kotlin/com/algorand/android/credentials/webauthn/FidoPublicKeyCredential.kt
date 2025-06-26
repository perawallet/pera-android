package com.algorand.android.credentials.webauthn

import com.algorand.android.credentials.encoding.b64Encode
import org.json.JSONObject

class FidoPublicKeyCredential(
    val rawId: ByteArray,
    val response: AuthenticatorResponse,
    val authenticatorAttachment: String,
) {

    fun json(): String {
        val encodedId = b64Encode(rawId)
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
}

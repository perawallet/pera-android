package com.algorand.android.credentials.webauthn

import org.json.JSONObject

interface AuthenticatorResponse {
    var clientJson: JSONObject
    fun json(): JSONObject
}

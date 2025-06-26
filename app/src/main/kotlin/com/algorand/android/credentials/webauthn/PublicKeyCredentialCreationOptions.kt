package com.algorand.android.credentials.webauthn

import com.algorand.android.credentials.encoding.b64Decode
import org.json.JSONObject

class PublicKeyCredentialCreationOptions(requestJson: String) {
    private val json: JSONObject = JSONObject(requestJson)

    val rp: PublicKeyCredentialRpEntity
    val user: PublicKeyCredentialUserEntity
    val challenge: ByteArray
    private val pubKeyCredParams: List<PublicKeyCredentialParameters>

    private var timeout: Long
    private var excludeCredentials: List<PublicKeyCredentialDescriptor>
    private var authenticatorSelection: AuthenticatorSelectionCriteria
    private var attestation: String

    init {
        val challengeString = json.getString("challenge")
        challenge = b64Decode(challengeString)
        val rpJson = json.getJSONObject("rp")
        rp = PublicKeyCredentialRpEntity(rpJson.getString("name"), rpJson.getString("id"))
        val rpUser = json.getJSONObject("user")
        val userId = b64Decode(rpUser.getString("id"))
        user = PublicKeyCredentialUserEntity(
            rpUser.getString("name"), userId, rpUser.getString("displayName"),
        )
        val pubKeyCredParamsJson = json.getJSONArray("pubKeyCredParams")
        val pubKeyCredParamsTmp: MutableList<PublicKeyCredentialParameters> = mutableListOf()
        for (i in 0 until pubKeyCredParamsJson.length()) {
            val e = pubKeyCredParamsJson.getJSONObject(i)
            pubKeyCredParamsTmp.add(
                PublicKeyCredentialParameters(
                    e.getString("type"),
                    e.getLong("alg"),
                ),
            )
        }
        pubKeyCredParams = pubKeyCredParamsTmp.toList()

        timeout = json.optLong("timeout", 0)

        excludeCredentials = emptyList()
        authenticatorSelection = AuthenticatorSelectionCriteria("platform", "required")
        attestation = json.optString("attestation", "none")
    }
}

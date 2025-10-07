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
 * This class is a duplicated version of the original [androidx.credentials.webauthn.PublicKeyCredentialCreationOptions]
 * from the WebAuthn library, which is restricted to library-only usage.
 *
 * It was duplicated intentionally to simplify integration and avoid the need to create
 * multiple sub-models and data mappers that would otherwise be required to work around
 * its restricted visibility.
 *
 * The functionality and structure are preserved as-is for compatibility and maintainability.
 */
internal class PublicKeyCredentialCreationOptions(requestJson: String) {
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
        challenge = WebAuthnUtils.b64Decode(challengeString)
        val rpJson = json.getJSONObject("rp")
        rp = PublicKeyCredentialRpEntity(rpJson.getString("name"), rpJson.getString("id"))
        val rpUser = json.getJSONObject("user")
        val userId = WebAuthnUtils.b64Decode(rpUser.getString("id"))
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

    internal data class PublicKeyCredentialRpEntity(
        val name: String,
        val id: String,
    )

    internal data class PublicKeyCredentialUserEntity(
        val name: String,
        val id: ByteArray,
        val displayName: String,
    ) {
        val userHandle: String
            get() = name
    }

    private data class PublicKeyCredentialParameters(
        val type: String,
        val alg: Long,
    )

    private data class PublicKeyCredentialDescriptor(
        val type: String,
        val id: ByteArray,
        val transports: List<String>,
    )

    private data class AuthenticatorSelectionCriteria(
        val authenticatorAttachment: String,
        val residentKey: String,
        val requireResidentKey: Boolean = false,
        val userVerification: String = "preferred",
    )
}

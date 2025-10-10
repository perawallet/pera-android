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

package com.algorand.android.credentials.passkeys.ui.viewmodel

import androidx.credentials.CreatePublicKeyCredentialResponse
import com.algorand.android.credentials.BuildConfig
import com.algorand.android.credentials.passkeys.domain.model.AuthenticatorAttestationResponse
import com.algorand.android.credentials.passkeys.domain.model.AuthenticatorFlags
import com.algorand.android.credentials.passkeys.domain.model.CreatePublicKeyCredentialResponseArgs
import com.algorand.android.credentials.passkeys.domain.model.CreatePublicKeyCredentialResponseData
import com.algorand.android.credentials.passkeys.domain.model.FidoPublicKeyCredential
import com.algorand.android.credentials.passkeys.foundation.Cbor
import com.algorand.android.credentials.passkeys.foundation.CoseMapper
import java.security.interfaces.ECPublicKey
import java.util.UUID
import javax.inject.Inject

internal class DefaultCreatePublicKeyCredentialResponseProcessor @Inject constructor(
    private val coseMapper: CoseMapper,
) : CreatePublicKeyCredentialResponseProcessor {

    override fun invoke(args: CreatePublicKeyCredentialResponseArgs): CreatePublicKeyCredentialResponseData {
        val response = constructWebAuthnResponse(args)
        val credential = FidoPublicKeyCredential(args.credentialId, response)
        return CreatePublicKeyCredentialResponseData(
            credentialId = args.credentialId,
            response = CreatePublicKeyCredentialResponse(credential.json())
        )
    }

    private fun constructWebAuthnResponse(
        args: CreatePublicKeyCredentialResponseArgs
    ): AuthenticatorAttestationResponse {
        return with(args) {
            val coseKey = coseMapper.mapPublicKeyToCose(keyPair.public as ECPublicKey)
            val spki = coseMapper.mapCoseKeyToSpki(coseKey)

            AuthenticatorAttestationResponse(
                aaguid = UUID.fromString(BuildConfig.PERA_AAGUID),
                requestOptions = request,
                credentialId = credentialId,
                credentialPublicKey = Cbor().encode(coseKey),
                origin = appInfoOrigin,
                authFlags = AuthenticatorFlags(),
                packageName = appInfo.packageName,
                clientDataHash = clientDataHash,
                spki = spki,
            )
        }
    }
}

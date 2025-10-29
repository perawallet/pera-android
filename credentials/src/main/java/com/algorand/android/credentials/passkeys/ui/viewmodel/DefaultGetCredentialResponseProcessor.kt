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

import androidx.credentials.GetCredentialResponse
import androidx.credentials.PublicKeyCredential
import com.algorand.android.credentials.passkeys.domain.Bip39SignManager
import com.algorand.android.credentials.passkeys.domain.model.AuthenticatorAssertionResponse
import com.algorand.android.credentials.passkeys.domain.model.AuthenticatorFlags
import com.algorand.android.credentials.passkeys.domain.model.FidoPublicKeyCredential
import com.algorand.android.credentials.passkeys.domain.usecase.SetPasskeyLastUsedTime
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel.GetCredentialsParams
import com.algorand.wallet.utils.date.TimeProvider
import javax.inject.Inject

internal class DefaultGetCredentialResponseProcessor @Inject constructor(
    private val bip39SignManager: Bip39SignManager,
    private val setPasskeyLastUsedTime: SetPasskeyLastUsedTime,
    private val timeProvider: TimeProvider
) : GetCredentialResponseProcessor {

    override suspend fun getResponseWithSignature(params: GetCredentialsParams): GetCredentialResponse {
        var callingOrigin = params.origin
        if (params.callingAppInfo != null) {
            callingOrigin = params.callingAppInfo
        }

        val authAssertionResponse = getAuthAssertionResponse(params, callingOrigin).apply {
            // QR code routes seem to have no / but mobile browser embedded routes seem to add the trailing /
    val origin = params.origin.removeSuffix("/")
            signature = bip39SignManager
                .sign(params.bip44Address, origin, params.username, dataToSign())
                ?: byteArrayOf()
        }
        setPasskeyLastUsedTime(params.credId, timeProvider.getCurrentTimeMillis())
        val fidoResponse = FidoPublicKeyCredential(params.credId, authAssertionResponse)
        return GetCredentialResponse(PublicKeyCredential(fidoResponse.json()))
    }

    private fun getAuthAssertionResponse(params: GetCredentialsParams, origin: String): AuthenticatorAssertionResponse {
        return AuthenticatorAssertionResponse(
            requestOptions = params.request,
            origin = origin,
            authFlags = AuthenticatorFlags(),
            userHandle = params.userId,
            packageName = params.packageName,
            clientDataHash = params.clientDataHash,
        )
    }
}

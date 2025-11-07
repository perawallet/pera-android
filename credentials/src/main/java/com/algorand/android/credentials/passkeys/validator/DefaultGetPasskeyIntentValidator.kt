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

package com.algorand.android.credentials.passkeys.validator

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.provider.PendingIntentHandler
import androidx.credentials.provider.ProviderGetCredentialRequest
import com.algorand.android.credentials.passkeys.domain.model.Passkey
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialRequestOptions
import com.algorand.android.credentials.passkeys.domain.usecase.GetPasskeyByCredentialId
import com.algorand.android.credentials.passkeys.ui.PasskeyProviderService
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
internal class DefaultGetPasskeyIntentValidator @Inject constructor(
    private val appInfoValidator: CallingAppInfoValidator,
    private val getPasskeyByCredentialId: GetPasskeyByCredentialId
) : GetPasskeyIntentValidator {

    override suspend fun validate(intent: Intent): GetPasskeyIntentValidationResult {
        val request = PendingIntentHandler.Companion.retrieveProviderGetCredentialRequest(intent)
        val requestExtras = intent.getBundleExtra(PasskeyProviderService.Companion.EXTRA_INTENT_DATA_KEY)
        val credentialIdB64 = requestExtras?.getString(PasskeyProviderService.Companion.CRED_ID_KEY)

        if (request == null || credentialIdB64.isNullOrEmpty()) {
            return GetPasskeyIntentValidationResult.UnableToExtractData
        }

        val publicKeyCredentialOption = request.credentialOptions.firstOrNull()
        val publicKeyRequest = publicKeyCredentialOption as? GetPublicKeyCredentialOption
        if (publicKeyRequest == null) {
            return GetPasskeyIntentValidationResult.InvalidRequestType
        }

        return getIntentResultValidatingAppInfo(request, publicKeyRequest, credentialIdB64)
    }

    private suspend fun getIntentResultValidatingAppInfo(
        request: ProviderGetCredentialRequest,
        publicKeyRequest: GetPublicKeyCredentialOption,
        credId: String
    ): GetPasskeyIntentValidationResult {
        val publicKeyRequestOptions = PublicKeyCredentialRequestOptions(publicKeyRequest.requestJson)
        val validationResult = appInfoValidator.validateCallingApp(publicKeyRequestOptions.rpId, request.callingAppInfo)
        return when (validationResult) {
            is AppInfoValidationResult.AppInfoNotFound -> GetPasskeyIntentValidationResult.AppInfoNotFound
            is AppInfoValidationResult.FailedToValidateRP -> GetPasskeyIntentValidationResult.FailedToValidateRP
            is AppInfoValidationResult.FailedToValidateOrigin -> GetPasskeyIntentValidationResult.FailedToValidateOrigin
            is AppInfoValidationResult.Success -> {
                val passkey = getPasskeyByCredentialId(credId)
                if (passkey == null) {
                    GetPasskeyIntentValidationResult.PasskeyNotFound
                } else {
                    val params = getGetCredentialsParams(
                        request,
                        publicKeyRequest,
                        publicKeyRequestOptions,
                        validationResult.callingAppInfoOrigin,
                        passkey
                    )
                    GetPasskeyIntentValidationResult.Success(request, params)
                }
            }
        }
    }

    private fun getGetCredentialsParams(
        request: ProviderGetCredentialRequest,
        publicKeyRequest: GetPublicKeyCredentialOption,
        publicKeyRequestOptions: PublicKeyCredentialRequestOptions,
        appInfoOrigin: String,
        passkey: Passkey
    ): GetPasskeyViewModel.GetCredentialsParams {
        return GetPasskeyViewModel.GetCredentialsParams(
            bip44Address = passkey.bip44Address,
            credId = passkey.credId,
            origin = appInfoOrigin,
            request = publicKeyRequestOptions,
            userId = passkey.userId,
            username = passkey.username,
            packageName = request.callingAppInfo.packageName,
            callingAppInfo = appInfoOrigin,
            clientDataHash = publicKeyRequest.clientDataHash.takeIf { appInfoOrigin.isNotBlank() }
        )
    }
}

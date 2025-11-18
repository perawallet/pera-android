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
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.provider.PendingIntentHandler
import androidx.credentials.provider.ProviderCreateCredentialRequest
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialCreationOptions
import com.algorand.android.credentials.passkeys.domain.usecase.DoesPasskeyExist
import com.algorand.android.credentials.passkeys.ui.PasskeyProviderService
import com.algorand.android.credentials.passkeys.ui.mapper.CreatePasskeyParamsMapper
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult
import com.algorand.android.credentials.passkeys.validator.AppInfoValidationResult.AppInfoNotFound
import com.algorand.android.credentials.passkeys.validator.AppInfoValidationResult.FailedToValidateOrigin
import com.algorand.android.credentials.passkeys.validator.AppInfoValidationResult.FailedToValidateRP
import com.algorand.android.credentials.passkeys.validator.AppInfoValidationResult.Success
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
internal class DefaultCreatePasskeyIntentValidator @Inject constructor(
    private val appInfoValidator: CallingAppInfoValidator,
    private val createPasskeyParamsMapper: CreatePasskeyParamsMapper,
    private val doesPasskeyExist: DoesPasskeyExist
) : CreatePasskeyIntentValidator {

    override suspend fun validate(intent: Intent): CreatePasskeyIntentValidationResult {
        val createPasskeyRequest = PendingIntentHandler.retrieveProviderCreateCredentialRequest(intent)
        val requestExtras = intent.getBundleExtra(PasskeyProviderService.EXTRA_INTENT_DATA_KEY)
        val bip44Address = requestExtras?.getString(PasskeyProviderService.BIP44ADDRESS)
        if (createPasskeyRequest == null || bip44Address == null) {
            return CreatePasskeyIntentValidationResult.UnableToExtractData
        }

        val biometricPromptResult = createPasskeyRequest.biometricPromptResult
        if (biometricPromptResult?.authenticationError != null) {
            val error = biometricPromptResult.authenticationError!!
            val message = error.errorMsg?.toString().orEmpty()
            return CreatePasskeyIntentValidationResult.BiometricError(error.errorCode, message)
        }

        return if (createPasskeyRequest.callingRequest is CreatePublicKeyCredentialRequest) {
            getIntentResultValidatingAppInfo(createPasskeyRequest, bip44Address)
        } else {
            CreatePasskeyIntentValidationResult.InvalidRequestType
        }
    }

    private suspend fun getIntentResultValidatingAppInfo(
        createPasskeyRequest: ProviderCreateCredentialRequest,
        bip44Address: String
    ): CreatePasskeyIntentValidationResult {
        val publicKeyRequest = createPasskeyRequest.callingRequest as CreatePublicKeyCredentialRequest
        val requestOptions = PublicKeyCredentialCreationOptions(publicKeyRequest.requestJson)

        if (doesPasskeyExist(requestOptions.rp.id, requestOptions.user.name, bip44Address)) {
            return CreatePasskeyIntentValidationResult.ExistingPasskey
        }

        val validationResult = appInfoValidator.validateCallingApp(
            requestOptions.rp.id,
            createPasskeyRequest.callingAppInfo
        )
        return when (validationResult) {
            is AppInfoNotFound -> CreatePasskeyIntentValidationResult.AppInfoNotFound
            is FailedToValidateRP -> CreatePasskeyIntentValidationResult.FailedToValidateRP
            is FailedToValidateOrigin -> CreatePasskeyIntentValidationResult.FailedToValidateOrigin
            is Success -> {
                val appInfoOrigin = validationResult.callingAppInfoOrigin
                val params = createPasskeyParamsMapper(createPasskeyRequest, bip44Address, appInfoOrigin)
                CreatePasskeyIntentValidationResult.Success(createPasskeyRequest, params)
            }
        }
    }
}

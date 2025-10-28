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

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.provider.CallingAppInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.credentials.R
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialCreationOptions
import com.algorand.android.credentials.passkeys.domain.usecase.AddNewPasskey
import com.algorand.android.credentials.passkeys.ui.mapper.CreatePublicKeyCredentialResponseArgsMapper
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.AppInfoNotFound
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.BiometricError
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.ExistingPasskey
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.FailedToValidateOrigin
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.FailedToValidateRP
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.InvalidRequestType
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.Success
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyIntentValidationResult.UnableToExtractData
import com.algorand.android.credentials.passkeys.validator.CreatePasskeyIntentValidator
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@HiltViewModel
internal class CreatePasskeyViewModel @Inject constructor(
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val addNewPasskey: AddNewPasskey,
    private val createPublicKeyCredentialResponseProcessor: CreatePublicKeyCredentialResponseProcessor,
    private val createPublicKeyCredentialResponseArgsMapper: CreatePublicKeyCredentialResponseArgsMapper,
    private val createPasskeyIntentValidator: CreatePasskeyIntentValidator
) : ViewModel(), EventViewModel<CreatePasskeyViewModel.ViewEvent> by eventDelegate {

    fun processIntent(intent: Intent) {
        viewModelScope.launch {
            val result = createPasskeyIntentValidator.validate(intent)
            when (result) {
                AppInfoNotFound -> finishWithError(R.string.calling_app_info_not_found)
                FailedToValidateOrigin -> finishWithError(R.string.failed_to_validate_origin)
                FailedToValidateRP -> finishWithError(R.string.failed_to_validate_rp)
                InvalidRequestType -> finishWithError(R.string.unexpected_create_request_found)
                UnableToExtractData -> finishWithError(R.string.unable_to_extract_data)
                ExistingPasskey -> finishWithError(R.string.this_passkey_is_already)
                is BiometricError -> finishWithBiometricError(result)
                is Success -> {
                    if (result.request.biometricPromptResult?.isSuccessful == true) {
                        createPasskey(result.params)
                    } else {
                        eventDelegate.sendEvent(ViewEvent.AuthenticateCreatePasskeyWithBiometrics(result.params))
                    }
                }
            }
        }
    }

    private suspend fun finishWithError(errorResId: Int) {
        eventDelegate.sendEvent(ViewEvent.FinishActivityWithCreateError(errorResId))
    }

    private suspend fun finishWithBiometricError(result: BiometricError) {
        eventDelegate.sendEvent(ViewEvent.FinishActivityWithCreateBiometricError(result.code, result.message))
    }

    fun createPasskey(params: CreatePasskeyParams) {
        viewModelScope.launch {
            with(params) {
                val args = createPublicKeyCredentialResponseArgsMapper(params, appInfoOrigin)
                val responseData = createPublicKeyCredentialResponseProcessor(args)
                addNewPasskey(bip44Address, requestOptions, responseData.credentialId)
                eventDelegate.sendEvent(ViewEvent.SetCreateResponseAndFinishActivity(responseData.response))
            }
        }
    }

    sealed interface ViewEvent {
        data class FinishActivityWithCreateError(val errorResId: Int) : ViewEvent
        data class FinishActivityWithCreateBiometricError(val errorCode: Int, val errorMessage: String) : ViewEvent
        data class AuthenticateCreatePasskeyWithBiometrics(val params: CreatePasskeyParams) : ViewEvent
        data class SetCreateResponseAndFinishActivity(val response: CreatePublicKeyCredentialResponse) : ViewEvent
    }

    data class CreatePasskeyParams(
        val requestOptions: PublicKeyCredentialCreationOptions,
        val callingAppInfo: CallingAppInfo,
        val clientDataHash: ByteArray?,
        val bip44Address: String,
        val appInfoOrigin: String
    ) {
        val rpId: String
            get() = requestOptions.rp.id
    }
}

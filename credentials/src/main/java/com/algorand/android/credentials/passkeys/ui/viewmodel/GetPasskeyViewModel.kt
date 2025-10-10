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
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.credentials.R
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialRequestOptions
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult.AppInfoNotFound
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult.FailedToValidateOrigin
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult.FailedToValidateRP
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult.InvalidRequestType
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult.PasskeyNotFound
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult.Success
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyIntentValidationResult.UnableToExtractData
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel.ViewEvent
import com.algorand.android.credentials.passkeys.validator.GetPasskeyIntentValidator
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class GetPasskeyViewModel @Inject constructor(
    private val getPasskeyIntentValidator: GetPasskeyIntentValidator,
    private val getCredentialResponseProcessor: GetCredentialResponseProcessor,
    private val eventDelegate: EventDelegate<ViewEvent>
) : ViewModel(), EventViewModel<ViewEvent> by eventDelegate {

    fun processIntent(intent: Intent) {
        viewModelScope.launch {
            val result = getPasskeyIntentValidator.validate(intent)
            when (result) {
                AppInfoNotFound -> finishWithError(R.string.calling_app_info_not_found)
                FailedToValidateOrigin -> finishWithError(R.string.failed_to_validate_origin)
                FailedToValidateRP -> finishWithError(R.string.failed_to_validate_rp)
                InvalidRequestType -> finishWithError(R.string.unexpected_create_request_found)
                UnableToExtractData -> finishWithError(R.string.unable_to_extract_data)
                PasskeyNotFound -> finishWithError(R.string.requested_credential_not_found)
                is Success -> {
                    if (result.request.biometricPromptResult?.isSuccessful == true) {
                        createGetCredentialResponse(result.params)
                    } else {
                        eventDelegate.sendEvent(ViewEvent.AuthenticateGetPasskeyWithBiometrics(result.params))
                    }
                }
            }
        }
    }

    fun createGetCredentialResponse(params: GetCredentialsParams) {
        viewModelScope.launch {
            val response = getCredentialResponseProcessor.getResponseWithSignature(params)
            eventDelegate.sendEvent(ViewEvent.SetGetResponseAndFinishActivity(response))
        }
    }

    private suspend fun finishWithError(errorResId: Int) {
        eventDelegate.sendEvent(ViewEvent.FinishActivityWithGetError(errorResId))
    }

    sealed interface ViewEvent {
        data class FinishActivityWithGetError(val errorResId: Int) : ViewEvent
        data class AuthenticateGetPasskeyWithBiometrics(val params: GetCredentialsParams) : ViewEvent
        data class SetGetResponseAndFinishActivity(val response: GetCredentialResponse) : ViewEvent
    }

    data class GetCredentialsParams(
        val bip39Address: String,
        val credId: String,
        val origin: String,
        val request: PublicKeyCredentialRequestOptions,
        val userId: String,
        val username: String,
        val packageName: String,
        val callingAppInfo: String?,
        val clientDataHash: ByteArray?,
    )
}

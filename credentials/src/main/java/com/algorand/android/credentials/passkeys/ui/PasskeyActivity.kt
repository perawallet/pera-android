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

package com.algorand.android.credentials.passkeys.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.algorand.android.credentials.R
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel.CreatePasskeyParams
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel.ViewEvent.AuthenticateCreatePasskeyWithBiometrics
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel.ViewEvent.FinishActivityWithCreateBiometricError
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel.ViewEvent.FinishActivityWithCreateError
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel.ViewEvent.SetCreateResponseAndFinishActivity
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel.GetCredentialsParams
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel.ViewEvent.AuthenticateGetPasskeyWithBiometrics
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel.ViewEvent.FinishActivityWithGetError
import com.algorand.android.credentials.passkeys.ui.viewmodel.GetPasskeyViewModel.ViewEvent.SetGetResponseAndFinishActivity
import com.algorand.android.credentials.passkeys.ui.PasskeyProviderService.Companion.CREATE_PASSKEY_INTENT
import com.algorand.android.credentials.passkeys.ui.PasskeyProviderService.Companion.GET_PASSKEY_INTENT
import com.algorand.android.credentials.passkeys.ui.biometric.PasskeyBiometricAuthenticator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@AndroidEntryPoint
class PasskeyActivity : FragmentActivity(),
    ActivityCredentialRequestResolver by DefaultActivityCredentialRequestResolver() {

    private val createPasskeyViewModel by viewModels<CreatePasskeyViewModel>()
    private val getPasskeyViewModel by viewModels<GetPasskeyViewModel>()

    private val createPasskeyViewEventCollector: suspend (CreatePasskeyViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is FinishActivityWithCreateError -> finishWithCreateCredentialError(getString(event.errorResId))
            is SetCreateResponseAndFinishActivity -> finishWithCreateCredentialResponse(event.response)
            is AuthenticateCreatePasskeyWithBiometrics -> authenticateCreatePasskeyWithBiometrics(event.params)
            is FinishActivityWithCreateBiometricError -> {
                val errorMessage = getString(R.string.biometric_error_message, event.errorCode, event.errorMessage)
                finishWithCreateCredentialError(errorMessage)
            }
        }
    }

    private val getPasskeyViewEventCollector: suspend (GetPasskeyViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is FinishActivityWithGetError -> finishWithGetCredentialError(getString(event.errorResId))
            is SetGetResponseAndFinishActivity -> finishWithGetCredentialResponse(event.response)
            is AuthenticateGetPasskeyWithBiometrics -> authenticateGetPasskeyWithBiometrics(event.params)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeRequestResolver(this)
        when (intent.action) {
            CREATE_PASSKEY_INTENT -> processCreatePasskeyRequest()
            GET_PASSKEY_INTENT -> processGetPasskeyRequest()
        }
    }

    private fun processCreatePasskeyRequest() {
        observeCreatePasskeyViewEvents()
        createPasskeyViewModel.processIntent(intent)
    }

    private fun processGetPasskeyRequest() {
        observeGetPasskeyViewEvents()
        getPasskeyViewModel.processIntent(intent)
    }

    private fun authenticateCreatePasskeyWithBiometrics(params: CreatePasskeyParams) {
        PasskeyBiometricAuthenticator(onFinishActivity = { finish() }) {
            createPasskeyViewModel.createPasskey(params)
        }.authenticate(this)
    }

    private fun authenticateGetPasskeyWithBiometrics(params: GetCredentialsParams) {
        PasskeyBiometricAuthenticator(onFinishActivity = { finish() }) {
            getPasskeyViewModel.createGetCredentialResponse(params)
        }.authenticate(this)
    }

    private fun observeCreatePasskeyViewEvents() {
        lifecycleScope.launch {
            createPasskeyViewModel.viewEvent
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collectLatest(createPasskeyViewEventCollector)
        }
    }

    private fun observeGetPasskeyViewEvents() {
        lifecycleScope.launch {
            getPasskeyViewModel.viewEvent
                .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collectLatest(getPasskeyViewEventCollector)
        }
    }
}

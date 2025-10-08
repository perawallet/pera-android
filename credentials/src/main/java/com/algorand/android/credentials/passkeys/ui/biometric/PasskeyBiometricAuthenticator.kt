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

package com.algorand.android.credentials.passkeys.ui.biometric

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.algorand.android.credentials.R

internal class PasskeyBiometricAuthenticator(
    private val onFinishActivity: () -> Unit,
    private val onSuccess: () -> Unit
) : BiometricPrompt.AuthenticationCallback() {

    fun authenticate(activity: FragmentActivity) {
        val biometricPrompt = BiometricPrompt(activity, activity.mainExecutor, this)
        authenticate(activity, biometricPrompt)
    }

    private fun authenticate(activity: FragmentActivity, biometricPrompt: BiometricPrompt) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.resources.getString(R.string.use_your_screen_lock))
            .setSubtitle(activity.resources.getString(R.string.use_your_fingerprint_to_continue))
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        onFinishActivity()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        onFinishActivity()
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        onSuccess()
    }
}

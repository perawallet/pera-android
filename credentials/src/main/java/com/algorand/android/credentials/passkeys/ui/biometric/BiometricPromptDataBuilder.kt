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

import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.provider.BiometricPromptData

@RequiresApi(Build.VERSION_CODES.R)
internal object BiometricPromptDataBuilder {

    private const val ALLOWED_AUTHENTICATORS = BIOMETRIC_WEAK or BIOMETRIC_STRONG or DEVICE_CREDENTIAL

    fun getDefaultPromptData(): BiometricPromptData {
        return BiometricPromptData(
            cryptoObject = null,
            allowedAuthenticators = ALLOWED_AUTHENTICATORS
        )
    }
}

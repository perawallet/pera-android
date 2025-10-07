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

import androidx.credentials.provider.CallingAppInfo
import com.algorand.android.credentials.passkeys.domain.WebAuthnUtils
import com.algorand.android.credentials.passkeys.validator.domain.usecase.GetCallingAppOriginCheckingGpmAllowlist
import com.algorand.android.credentials.passkeys.validator.domain.usecase.IsAssetLinksValid
import javax.inject.Inject

internal class PasskeyCallingAppInfoValidator @Inject constructor(
    private val isAssetLinksValid: IsAssetLinksValid,
    private val getCallingAppOriginCheckingGpmAllowlist: GetCallingAppOriginCheckingGpmAllowlist,
) : CallingAppInfoValidator {

    override suspend fun validateCallingApp(rpId: String, callingAppInfo: CallingAppInfo?): AppInfoValidationResult {
        if (callingAppInfo == null) return AppInfoValidationResult.AppInfoNotFound
        return if (isWebRequest(callingAppInfo)) {
            getCallingAppOriginCheckingGpmAllowlist(callingAppInfo).use(
                onSuccess = { origin ->
                    AppInfoValidationResult.Success(origin)
                },
                onFailed = { exception, _ ->
                    AppInfoValidationResult.FailedToValidateOrigin(exception)
                }
            )
        } else {
            isAssetLinksValid(rpId, callingAppInfo).use(
                onSuccess = { isValid ->
                    val origin = WebAuthnUtils.appInfoToOrigin(callingAppInfo)
                    if (isValid) AppInfoValidationResult.Success(origin) else AppInfoValidationResult.FailedToValidateRP
                },
                onFailed = { _, _ ->
                    AppInfoValidationResult.FailedToValidateRP
                }
            )
        }
    }

    private fun isWebRequest(callingAppInfo: CallingAppInfo): Boolean {
        try {
            callingAppInfo.getOrigin(INVALID_ALLOWLIST)
        } catch (e: IllegalStateException) {
            return true
        }
        return false
    }

    private companion object {

        const val INVALID_ALLOWLIST = "{\"apps\": [\n" +
            "   {\n" +
            "      \"type\": \"android\", \n" +
            "      \"info\": {\n" +
            "         \"package_name\": \"androidx.credentials.test\",\n" +
            "         \"signatures\" : [\n" +
            "         {\"build\": \"release\",\n" +
            "             \"cert_fingerprint_sha256\": \"HELLO\"\n" +
            "         },\n" +
            "         {\"build\": \"ud\",\n" +
            "         \"cert_fingerprint_sha256\": \"YELLOW\"\n" +
            "         }]\n" +
            "      }\n" +
            "    }\n" +
            "]}\n" +
            "\n"
    }
}

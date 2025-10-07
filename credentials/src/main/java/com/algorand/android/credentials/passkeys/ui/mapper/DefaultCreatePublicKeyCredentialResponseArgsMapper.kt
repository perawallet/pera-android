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

package com.algorand.android.credentials.passkeys.ui.mapper

import com.algorand.android.credentials.passkeys.domain.PasskeyManager
import com.algorand.android.credentials.passkeys.domain.model.CreatePublicKeyCredentialResponseArgs
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel.CreatePasskeyParams
import javax.inject.Inject

internal class DefaultCreatePublicKeyCredentialResponseArgsMapper @Inject constructor(
    private val passkeyManager: PasskeyManager
) : CreatePublicKeyCredentialResponseArgsMapper {

    override suspend fun invoke(
        params: CreatePasskeyParams,
        appInfoOrigin: String
    ): CreatePublicKeyCredentialResponseArgs {
        with(params) {
            val userHandle = requestOptions.user.name
            val keyPair = passkeyManager.derivePasskey(seedId, appInfoOrigin, userHandle)
            return CreatePublicKeyCredentialResponseArgs(
                keyPair = keyPair,
                credentialId = passkeyManager.deriveCredentialId(keyPair),
                request = requestOptions,
                appInfoOrigin = appInfoOrigin,
                appInfo = callingAppInfo,
                clientDataHash = clientDataHash
            )
        }
    }
}

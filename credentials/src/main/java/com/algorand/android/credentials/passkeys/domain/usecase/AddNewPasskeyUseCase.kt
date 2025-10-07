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

package com.algorand.android.credentials.passkeys.domain.usecase

import com.algorand.android.credentials.passkeys.domain.model.AddPasskeyArgs
import com.algorand.android.credentials.passkeys.domain.repository.PasskeyRepository
import com.algorand.android.credentials.passkeys.domain.WebAuthnUtils
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialCreationOptions
import javax.inject.Inject

internal class AddNewPasskeyUseCase @Inject constructor(
    private val passkeyRepository: PasskeyRepository
) : AddNewPasskey {

    override suspend fun invoke(
        seedId: Int,
        requestOptions: PublicKeyCredentialCreationOptions,
        credId: ByteArray
    ) {
        val args = getAddPasskeyArgs(seedId, requestOptions, credId)
        passkeyRepository.addNewPasskey(args)
    }

    private fun getAddPasskeyArgs(
        seedId: Int,
        requestOptions: PublicKeyCredentialCreationOptions,
        credId: ByteArray
    ): AddPasskeyArgs {
        return AddPasskeyArgs(
            siteUrl = requestOptions.rp.id,
            siteName = requestOptions.rp.name,
            seedId = seedId,
            uid = WebAuthnUtils.b64Encode(requestOptions.user.id),
            username = requestOptions.user.name,
            displayName = requestOptions.user.displayName,
            credId = WebAuthnUtils.b64Encode(credId)
        )
    }
}

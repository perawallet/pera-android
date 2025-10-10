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

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.provider.ProviderCreateCredentialRequest
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialCreationOptions
import com.algorand.android.credentials.passkeys.ui.viewmodel.CreatePasskeyViewModel.CreatePasskeyParams
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
internal class DefaultCreatePasskeyParamsMapper @Inject constructor() : CreatePasskeyParamsMapper {

    override fun invoke(
        request: ProviderCreateCredentialRequest,
        bip44Address: String,
        appInfoOrigin: String
    ): CreatePasskeyParams {
        val publicKeyRequest = request.callingRequest as CreatePublicKeyCredentialRequest
        return with(publicKeyRequest) {
            CreatePasskeyParams(
                requestOptions = PublicKeyCredentialCreationOptions(requestJson),
                callingAppInfo = request.callingAppInfo,
                clientDataHash = clientDataHash,
                bip44Address = bip44Address,
                appInfoOrigin = appInfoOrigin
            )
        }
    }
}

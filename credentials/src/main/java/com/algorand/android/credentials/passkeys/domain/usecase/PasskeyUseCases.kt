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

import com.algorand.android.credentials.passkeys.domain.model.Passkey
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialCreationOptions
import kotlinx.coroutines.flow.Flow

fun interface GetAllPasskeysAsFlow {
    operator fun invoke(): Flow<List<Passkey>>
}

fun interface RemovePasskeyByCredentialId {
    suspend operator fun invoke(credId: String)
}

fun interface ClearAllPasskeys {
    suspend operator fun invoke()
}

internal fun interface GetSitePasskeyCount {
    suspend operator fun invoke(url: String): Int
}

internal fun interface GetSitePasskeys {
    suspend operator fun invoke(url: String): List<Passkey>
}

internal fun interface AddNewPasskey {
    suspend operator fun invoke(
        bip39Address: String,
        requestOptions: PublicKeyCredentialCreationOptions,
        credId: ByteArray
    )
}

internal fun interface GetPasskeyByCredentialId {
    suspend operator fun invoke(credentialId: String): Passkey?
}

internal fun interface SetPasskeyLastUsedTime {
    suspend operator fun invoke(credId: String, lastUsed: Long)
}

internal fun interface DoesPasskeyExist {
    suspend operator fun invoke(rpId: String, username: String, bip39Address: String): Boolean
}

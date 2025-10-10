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

package com.algorand.android.credentials.passkeys.domain.repository

import com.algorand.android.credentials.passkeys.domain.model.AddPasskeyArgs
import com.algorand.android.credentials.passkeys.domain.model.Passkey
import kotlinx.coroutines.flow.Flow

internal interface PasskeyRepository {
    fun getAllPasskeysAsFlow(): Flow<List<Passkey>>
    suspend fun getSitePasskeysCount(url: String): Int
    suspend fun getSitePasskeys(url: String): List<Passkey>
    suspend fun addNewPasskey(args: AddPasskeyArgs)
    suspend fun getPasskey(credId: String): Passkey?
    suspend fun removePasskeyByCredentialId(credId: String)
    suspend fun clearAllPasskeys()
    suspend fun setPasskeyLastUsedTime(credId: String, lastUsed: Long)
    suspend fun doesPasskeyExist(rpId: String, username: String, bip44Address: String): Boolean
}

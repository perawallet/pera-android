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

package com.algorand.android.credentials.passkeys.ui.builder

import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.provider.BeginGetCredentialRequest
import androidx.credentials.provider.BeginGetPublicKeyCredentialOption
import com.algorand.android.credentials.passkeys.domain.model.Passkey
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialRequestOptions
import com.algorand.android.credentials.passkeys.domain.usecase.GetSitePasskeys
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyCredentialEntry
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultPasskeyGetCredentialsEntryBuilder @Inject constructor(
    private val getSitePasskeys: GetSitePasskeys
) : PasskeyGetCredentialsEntryBuilder {

    override suspend fun buildEntries(request: BeginGetCredentialRequest): PeraResult<List<GetPasskeyCredentialEntry>> {
        val entries = request.beginGetCredentialOptions
            .filterIsInstance<BeginGetPublicKeyCredentialOption>()
            .mapNotNull { option -> getEntries(option) }
            .flatten()
        return if (entries.isEmpty()) {
            PeraResult.Error(NoCredentialException())
        } else {
            PeraResult.Success(entries)
        }
    }

    private suspend fun getEntries(option: BeginGetPublicKeyCredentialOption): List<GetPasskeyCredentialEntry>? {
        val siteUrl = PublicKeyCredentialRequestOptions(option.requestJson).rpId
        if (siteUrl.isBlank()) {
            return null
        }
        val passkeys = getSitePasskeys(siteUrl)
        return if (passkeys.isEmpty()) {
            null
        } else {
            mapToEntries(option, passkeys)
        }
    }

    private fun mapToEntries(
        option: BeginGetPublicKeyCredentialOption,
        passkeys: List<Passkey>
    ): List<GetPasskeyCredentialEntry> {
        return passkeys.map { passkey ->
            GetPasskeyCredentialEntry(
                option = option,
                credentialId = passkey.credId,
                username = passkey.username,
                userDisplayName = passkey.displayName
            )
        }
    }
}

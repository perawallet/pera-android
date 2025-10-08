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

import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.credentials.provider.BeginCreateCredentialRequest
import com.algorand.android.credentials.passkeys.domain.model.PublicKeyCredentialCreationOptions
import com.algorand.android.credentials.passkeys.domain.usecase.GetSitePasskeyCount
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyCredentialCreateEntry
import com.algorand.wallet.account.custom.domain.usecase.GetHdSeedCustomName
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeeds
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultPasskeyCreateCredentialEntryBuilder @Inject constructor(
    private val getAllHdSeeds: GetAllHdSeeds,
    private val getSitePasskeyCount: GetSitePasskeyCount,
    private val getHdSeedCustomName: GetHdSeedCustomName
) : PasskeyCreateCredentialEntryBuilder {

    override suspend fun buildEntries(
        request: BeginCreateCredentialRequest
    ): PeraResult<List<CreatePasskeyCredentialCreateEntry>> {
        val hdSeedIds = getAllHdSeeds()
        return if (hdSeedIds.isEmpty()) {
            PeraResult.Error(CreateCredentialNoCreateOptionException())
        } else {
            PeraResult.Success(createEntries(request, hdSeedIds))
        }
    }

    private suspend fun createEntries(
        request: BeginCreateCredentialRequest,
        hdSeeds: List<HdSeed>
    ): List<CreatePasskeyCredentialCreateEntry> {
        val registeredRelyingPartyPasskeyCount = getPasskeyCount(request)
        return hdSeeds.map { hdSeed ->
            CreatePasskeyCredentialCreateEntry(
                accountName = getHdSeedCustomName(hdSeed.seedId).orEmpty(),
                passkeyCount = registeredRelyingPartyPasskeyCount,
                seedId = hdSeed.seedId
            )
        }
    }

    private suspend fun getPasskeyCount(request: BeginCreateCredentialRequest): Int {
        val credentialOptions = getCredentialOptions(request)
        val relyingPartyUrl = credentialOptions?.rp?.id ?: return 0
        return getSitePasskeyCount(relyingPartyUrl)
    }

    private fun getCredentialOptions(request: BeginCreateCredentialRequest): PublicKeyCredentialCreationOptions? {
        return try {
            val requestJson = request.candidateQueryData.getString(BUNDLE_KEY)
            PublicKeyCredentialCreationOptions(requestJson.orEmpty())
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val BUNDLE_KEY = "androidx.credentials.BUNDLE_KEY_REQUEST_JSON"
    }
}

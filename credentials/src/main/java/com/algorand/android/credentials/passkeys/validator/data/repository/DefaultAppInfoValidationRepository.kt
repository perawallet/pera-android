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

package com.algorand.android.credentials.passkeys.validator.data.repository

import com.algorand.android.credentials.passkeys.validator.data.model.AssetLinkCheckResultResponse
import com.algorand.android.credentials.passkeys.validator.data.network.AssetLinksApiService
import com.algorand.android.credentials.passkeys.validator.data.network.GStaticApiService
import com.algorand.android.credentials.passkeys.validator.domain.model.AssetLinkCheckResult
import com.algorand.android.credentials.passkeys.validator.domain.repository.AppInfoValidationRepository
import com.algorand.wallet.foundation.PeraResult
import com.google.gson.JsonElement
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class DefaultAppInfoValidationRepository @Inject constructor(
    private val gStaticApiService: GStaticApiService,
    private val assetLinksApiService: AssetLinksApiService
) : AppInfoValidationRepository {

    override suspend fun getGpmPrivilegedAppAllowlist(): PeraResult<JsonElement> {
        return try {
            val allowlist = gStaticApiService.getPrivilegedAppAllowlist().body()
            if (allowlist == null) PeraResult.Error(Exception()) else PeraResult.Success(allowlist)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override suspend fun getAssetLinkCheckResult(
        url: String,
        pkgName: String,
        certId: String
    ): PeraResult<AssetLinkCheckResult> {
        return try {
            val responseJson = assetLinksApiService.getAssetLinksCheckResult(url, pkgName, certId)
            val response = Json.decodeFromString<AssetLinkCheckResultResponse>(responseJson)
            val result = AssetLinkCheckResult(response.linked)
            PeraResult.Success(result)
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }
}

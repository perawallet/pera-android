/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.info.data.service

import com.algorand.common.account.info.data.model.AccountAssetsResponse
import com.algorand.common.account.info.data.model.AccountInformationResponse
import com.algorand.common.account.info.data.model.RekeyedAccountsResponse
import com.algorand.common.foundation.PeraResult
import com.algorand.common.foundation.network.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class IndexerApiServiceImpl(
    private val client: HttpClient
) : IndexerApiService {

    override suspend fun getAccountInformation(
        publicKey: String,
        excludes: String,
        includeClosedAccounts: Boolean
    ): PeraResult<AccountInformationResponse> {
        return safeRequest {
            client.get("v2/accounts/$publicKey") {
                parameter("excludes", excludes)
                parameter("include-all", includeClosedAccounts)
            }
        }
    }

    override suspend fun getRekeyedAccounts(rekeyAdminAddress: String): PeraResult<RekeyedAccountsResponse> {
        return safeRequest {
            client.get("v2/accounts") {
                parameter("auth-addr", rekeyAdminAddress)
            }
        }
    }

    override suspend fun getAccountAssets(
        address: String,
        limit: Int,
        nextToken: String?
    ): PeraResult<AccountAssetsResponse> {
        return safeRequest {
            client.get("v2/accounts/$address/assets") {
                parameter("limit", limit)
                nextToken?.let { parameter("next", it) }
            }
        }
    }
}

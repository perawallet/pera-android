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

internal interface AccountInformationApiService {

    suspend fun getAccountInformation(
        publicKey: String,
        excludes: String,
        includeClosedAccounts: Boolean = false
    ): PeraResult<AccountInformationResponse>

    suspend fun getRekeyedAccounts(
        rekeyAdminAddress: String
    ): PeraResult<RekeyedAccountsResponse>

    suspend fun getAccountAssets(
        address: String,
        limit: Int,
        nextToken: String? = null
    ): PeraResult<AccountAssetsResponse>
}

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

package com.algorand.android.repository

import com.algorand.android.cache.AccountLocalCache
import com.algorand.android.models.AccountDetail
import com.algorand.android.models.AccountInformationResponse
import com.algorand.android.models.AccountsResponse
import com.algorand.android.models.Result
import com.algorand.android.network.IndexerApi
import com.algorand.android.network.safeApiCall
import com.algorand.android.utils.CacheResult
import java.net.HttpURLConnection
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val indexerApi: IndexerApi,
    private val accountLocalCache: AccountLocalCache
) {

    suspend fun getAccountInformation(
        publicKey: String,
        includeClosedAccounts: Boolean = false
    ): Result<AccountInformationResponse> = safeApiCall {
        requestAccountInformation(publicKey, includeClosedAccounts)
    }

    private suspend fun requestAccountInformation(
        publicKey: String,
        includeClosedAccounts: Boolean
    ): Result<AccountInformationResponse> {
        with(indexerApi.getAccountInformation(publicKey, includeClosedAccounts)) {
            val accountInformation = body()
            return if (isSuccessful && accountInformation != null) {
                Result.Success(accountInformation)
            } else {
                Result.Error(Exception(), code())
            }
        }
    }

    suspend fun getRekeyedAccounts(rekeyAdminAddress: String): Result<AccountsResponse> =
        safeApiCall { requestGetRekeyedAccounts(rekeyAdminAddress) }

    private suspend fun requestGetRekeyedAccounts(
        rekeyAdminAddress: String
    ): Result<AccountsResponse> {
        with(indexerApi.getRekeyedAccounts(rekeyAdminAddress)) {
            val accountInformationResponse = body()
            return if (isSuccessful && accountInformationResponse != null) {
                Result.Success(accountInformationResponse)
            } else {
                Result.Error(Exception())
            }
        }
    }

    suspend fun cacheAccountDetail(accountDetail: CacheResult.Success<AccountDetail>) {
        accountLocalCache.put(accountDetail)
    }

    suspend fun cacheAllAccountDetails(accountDetailKeyValuePairList: List<Pair<String, CacheResult<AccountDetail>>>) {
        accountLocalCache.putAll(accountDetailKeyValuePairList)
    }

    fun getCachedAccountDetail(publicKey: String): CacheResult<AccountDetail>? {
        return accountLocalCache.getOrNull(publicKey)
    }

    suspend fun clearAccountDetailCache() {
        accountLocalCache.clear()
    }

    companion object {
        const val ACCOUNT_NOT_FOUND_ERROR_CODE = HttpURLConnection.HTTP_NOT_FOUND
    }
}

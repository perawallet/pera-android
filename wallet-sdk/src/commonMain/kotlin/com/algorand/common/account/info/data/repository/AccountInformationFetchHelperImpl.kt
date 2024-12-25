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

package com.algorand.common.account.info.data.repository

import com.algorand.common.account.info.data.mapper.AccountInformationResponseMapper
import com.algorand.common.account.info.data.model.AccountInformationResponse
import com.algorand.common.account.info.data.model.IndexerAccountFetchRequestExcludes.ASSETS
import com.algorand.common.account.info.data.model.IndexerAccountFetchRequestExcludes.CREATED_APPS
import com.algorand.common.account.info.data.model.IndexerAccountFetchRequestExcludes.CREATED_ASSETS
import com.algorand.common.account.info.data.service.AccountInformationApiService
import com.algorand.common.foundation.PeraResult
import kotlinx.io.IOException

internal class AccountInformationFetchHelperImpl(
    private val indexerApi: AccountInformationApiService,
    private val accountAssetHoldingsFetchHelper: AccountAssetHoldingsFetchHelper,
    private val accountInformationResponseMapper: AccountInformationResponseMapper,
) : AccountInformationFetchHelper {

    override suspend fun fetchAccount(address: String): PeraResult<AccountInformationResponse> {
        val excludesQuery = IndexerAccountFetchRequestExcludesQueryBuilder.newBuilder()
            .addExclude(CREATED_ASSETS)
            .addExclude(CREATED_APPS)
            .build()
        return indexerApi.getAccountInformation(address, excludesQuery).use(
            onSuccess = {
                PeraResult.Success(it)
            },
            onFailed = { exception, code ->
                processFailedResponse(address, exception, code)
            }
        )
    }

    private suspend fun processFailedResponse(
        address: String,
        exception: Exception,
        errorCode: Int?
    ): PeraResult<AccountInformationResponse> {
        return when {
            errorCode == ACCOUNT_NOT_FOUND -> {
                PeraResult.Success(accountInformationResponseMapper.createEmptyAccount(address))
            }
            exception is IOException -> PeraResult.Error(exception, errorCode)
            else -> fetchAccountAndAssetsSeparately(address)
        }
    }

    private suspend fun fetchAccountAndAssetsSeparately(address: String): PeraResult<AccountInformationResponse> {
        val excludesQuery = IndexerAccountFetchRequestExcludesQueryBuilder.newBuilder()
            .addExclude(CREATED_ASSETS)
            .addExclude(CREATED_APPS)
            .addExclude(ASSETS)
            .build()
        return indexerApi.getAccountInformation(address, excludesQuery).use(
            onSuccess = { response ->
                fetchAssets(address, response)
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    private suspend fun fetchAssets(
        address: String,
        response: AccountInformationResponse
    ): PeraResult<AccountInformationResponse> {
        return accountAssetHoldingsFetchHelper.fetchAccountAssetHoldings(address).use(
            onSuccess = { assetHoldings ->
                val accountInfo = response.copy(
                    accountInformation = response.accountInformation?.copy(allAssetHoldingList = assetHoldings)
                )
                PeraResult.Success(accountInfo)
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    private companion object {
        const val ACCOUNT_NOT_FOUND = 404
    }
}

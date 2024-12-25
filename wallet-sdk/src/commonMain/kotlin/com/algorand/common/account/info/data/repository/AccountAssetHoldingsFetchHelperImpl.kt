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

import com.algorand.common.account.info.data.model.AssetHoldingResponse
import com.algorand.common.account.info.data.service.AccountInformationApiService
import com.algorand.common.foundation.PeraResult

internal class AccountAssetHoldingsFetchHelperImpl(
    private val indexerApi: AccountInformationApiService
) : AccountAssetHoldingsFetchHelper {

    override suspend fun fetchAccountAssetHoldings(accountAddress: String): PeraResult<List<AssetHoldingResponse>> {
        return fetch(accountAddress, null, mutableListOf())
    }

    private suspend fun fetch(
        address: String,
        nextToken: String?,
        holdings: MutableList<AssetHoldingResponse>
    ): PeraResult<List<AssetHoldingResponse>> {
        return indexerApi.getAccountAssets(address, ASSET_FETCH_LIMIT_PER_PAGE, nextToken).use(
            onSuccess = { response ->
                holdings.addAll(response.assets.orEmpty())
                if (response.nextToken != null && !response.assets.isNullOrEmpty()) {
                    fetch(address, response.nextToken, holdings)
                } else {
                    PeraResult.Success(holdings)
                }
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    companion object {
        private const val ASSET_FETCH_LIMIT_PER_PAGE = 5000
    }
}

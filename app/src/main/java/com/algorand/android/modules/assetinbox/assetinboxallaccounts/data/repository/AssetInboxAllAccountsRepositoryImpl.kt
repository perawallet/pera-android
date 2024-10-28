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

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.repository

import android.content.Context
import com.algorand.android.R
import com.algorand.android.banner.data.cache.AssetInboxLocalCache
import com.algorand.android.exceptions.RetrofitErrorHandler
import com.algorand.android.models.Result
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.mapper.AssetInboxAllAccountsMapper
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.data.service.AssetInboxAllAccountsApiService
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.repository.AssetInboxAllAccountsRepository
import com.algorand.android.network.requestWithHipoErrorHandler
import com.algorand.android.utils.CacheResult
import com.algorand.android.utils.toCsvString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class AssetInboxAllAccountsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetInboxAllAccountsApiService: AssetInboxAllAccountsApiService,
    private val retrofitErrorHandler: RetrofitErrorHandler,
    private val assetInboxAllAccountsMapper: AssetInboxAllAccountsMapper,
    private val assetInboxLocalCache: AssetInboxLocalCache
) : AssetInboxAllAccountsRepository {

    override suspend fun getAssetInboxAllAccounts(addresses: List<String>): Result<List<AssetInboxAllAccounts>> {
        val result = requestWithHipoErrorHandler(retrofitErrorHandler) {
            assetInboxAllAccountsApiService.getAssetInboxAllAccountsRequests(addresses.toCsvString())
        }
        if (result is Result.Error) return result
        val assetInboxAllAccountsResponse = (result as Result.Success).data
        val assetInboxAllAccountsList = assetInboxAllAccountsMapper(
            assetInboxAllAccountsResponse.assetInboxAllAccountsResponseList
        )
        return if (assetInboxAllAccountsList == null) {
            Result.Error(Exception(context.getString(R.string.failed_to_map_the_response)))
        } else {
            Result.Success(assetInboxAllAccountsList)
        }
    }

    override suspend fun getAssetInboxAllAccountsCacheFlow():
            StateFlow<HashMap<String, CacheResult<AssetInboxAllAccounts>>> {
        return assetInboxLocalCache.cacheMapFlow
    }

    override suspend fun getAssetInboxCountCacheFlow(): Flow<Int> {
        return assetInboxLocalCache.cacheMapFlow.map {
            it.values.sumOf {
                it.data?.requestCount ?: 0
            }
        }
    }

    override suspend fun clearAssetInboxAllAccountsCache() {
        assetInboxLocalCache.clear()
    }

    override suspend fun cacheAssetInboxAllAccounts(
        assetInboxAllAccountsList: List<CacheResult
        .Success<AssetInboxAllAccounts>>
    ) {
        assetInboxLocalCache.put(assetInboxAllAccountsList)
    }
}

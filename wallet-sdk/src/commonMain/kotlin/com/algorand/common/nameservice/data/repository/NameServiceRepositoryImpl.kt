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

package com.algorand.common.nameservice.data.repository

import com.algorand.common.nameservice.data.mapper.NameServiceMapper
import com.algorand.common.nameservice.data.mapper.NameServiceSearchResultMapper
import com.algorand.common.foundation.PeraResult
import com.algorand.common.foundation.cache.InMemoryLocalCache
import com.algorand.common.nameservice.data.model.SearchNameServiceRequestBody
import com.algorand.common.nameservice.data.service.NameServiceApiService
import com.algorand.common.nameservice.domain.model.NameService
import com.algorand.common.nameservice.domain.model.NameServiceSearchResult
import com.algorand.common.nameservice.domain.repository.NameServiceRepository

internal class NameServiceRepositoryImpl(
    private val nameServiceApiService: NameServiceApiService,
    private val nameServiceMapper: NameServiceMapper,
    private val inMemoryLocalCache: InMemoryLocalCache<String, NameService>,
    private val nameServiceSearchResultMapper: NameServiceSearchResultMapper
) : NameServiceRepository {

    override suspend fun initializeNameServiceCache(addresses: List<String>): PeraResult<List<NameService>> {
        return nameServiceApiService.fetchAccountsNameServices(SearchNameServiceRequestBody(addresses)).use(
            onSuccess = {
                val nameServices = nameServiceMapper(it.results.orEmpty())
                inMemoryLocalCache.putAll(nameServices.map { it.accountAddress to it })
                PeraResult.Success(nameServices)
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    override suspend fun getNameServiceSearchResults(query: String): PeraResult<List<NameServiceSearchResult>> {
        return nameServiceApiService.getNameServiceAccountAddresses(query).map { searchResponses ->
            searchResponses.results?.mapNotNull { response ->
                nameServiceSearchResultMapper(response)
            }.orEmpty()
        }
    }

    override suspend fun getNameService(address: String): NameService? {
        return inMemoryLocalCache[address]
    }
}

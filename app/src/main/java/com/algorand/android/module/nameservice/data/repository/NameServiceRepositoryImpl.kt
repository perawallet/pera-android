package com.algorand.android.module.nameservice.data.repository

import com.algorand.android.caching.InMemoryLocalCache
import com.algorand.android.foundation.PeraResult
import com.algorand.android.module.nameservice.data.mapper.NameServiceMapper
import com.algorand.android.module.nameservice.data.mapper.NameServiceSearchResultMapper
import com.algorand.android.module.nameservice.data.model.SearchNameServiceRequestBody
import com.algorand.android.module.nameservice.data.service.NameServiceApiService
import com.algorand.android.module.nameservice.domain.model.NameService
import com.algorand.android.module.nameservice.domain.model.NameServiceSearchResult
import com.algorand.android.module.nameservice.domain.repository.NameServiceRepository
import com.algorand.android.network_utils.exceptions.RetrofitErrorHandler
import com.algorand.android.network_utils.requestWithHipoErrorHandler
import javax.inject.Inject

internal class NameServiceRepositoryImpl @Inject constructor(
    private val nameServiceApiService: NameServiceApiService,
    private val hipoApiErrorHandler: RetrofitErrorHandler,
    private val nameServiceMapper: NameServiceMapper,
    private val inMemoryLocalCache: InMemoryLocalCache<String, NameService>,
    private val nameServiceSearchResultMapper: NameServiceSearchResultMapper
) : NameServiceRepository {

    override suspend fun initializeNameServiceCache(addresses: List<String>): PeraResult<List<NameService>> {
        return requestWithHipoErrorHandler(hipoApiErrorHandler) {
            nameServiceApiService.fetchAccountsNameServices(SearchNameServiceRequestBody(addresses))
        }.use(
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
        return requestWithHipoErrorHandler(hipoApiErrorHandler) {
            nameServiceApiService.getNameServiceAccountAddresses(query)
        }.map { searchResponses ->
            searchResponses.results?.mapNotNull { response ->
                nameServiceSearchResultMapper(response)
            }.orEmpty()
        }
    }

    override suspend fun getNameService(address: String): NameService? {
        return inMemoryLocalCache[address]
    }
}

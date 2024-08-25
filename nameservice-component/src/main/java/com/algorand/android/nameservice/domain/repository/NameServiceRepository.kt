package com.algorand.android.nameservice.domain.repository

import com.algorand.android.foundation.PeraResult
import com.algorand.android.nameservice.domain.model.NameService
import com.algorand.android.nameservice.domain.model.NameServiceSearchResult

internal interface NameServiceRepository {
    suspend fun initializeNameServiceCache(addresses: List<String>): PeraResult<List<NameService>>
    suspend fun getNameService(address: String): NameService?
    suspend fun getNameServiceSearchResults(query: String): PeraResult<List<NameServiceSearchResult>>
}

package com.algorand.android.module.nameservice.domain.repository

import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.nameservice.domain.model.NameService
import com.algorand.android.module.nameservice.domain.model.NameServiceSearchResult

internal interface NameServiceRepository {
    suspend fun initializeNameServiceCache(addresses: List<String>): PeraResult<List<NameService>>
    suspend fun getNameService(address: String): NameService?
    suspend fun getNameServiceSearchResults(query: String): PeraResult<List<NameServiceSearchResult>>
}

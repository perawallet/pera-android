package com.algorand.android.module.nameservice.domain.usecase

import com.algorand.android.foundation.PeraResult
import com.algorand.android.module.nameservice.domain.model.NameService
import com.algorand.android.module.nameservice.domain.model.NameServiceSearchResult

fun interface InitializeAccountNameService {
    suspend operator fun invoke(addresses: List<String>): PeraResult<List<NameService>>
}

fun interface GetAccountNameService {
    suspend operator fun invoke(address: String): NameService?
}

interface GetNameServiceSearchResults {
    suspend operator fun invoke(query: String): List<NameServiceSearchResult>
}
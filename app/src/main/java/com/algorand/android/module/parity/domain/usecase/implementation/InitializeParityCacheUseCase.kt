package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.module.parity.domain.usecase.*
import com.algorand.android.module.parity.domain.usecase.FetchAndCacheParity
import javax.inject.Inject

internal class InitializeParityCacheUseCase @Inject constructor(
    private val fetchAndCacheParity: FetchAndCacheParity
) : InitializeParityCache {

    override suspend fun invoke() {
        fetchAndCacheParity()
    }
}

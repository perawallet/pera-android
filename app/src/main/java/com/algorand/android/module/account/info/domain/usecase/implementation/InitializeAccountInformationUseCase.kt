package com.algorand.android.module.account.info.domain.usecase.implementation

import com.algorand.android.module.account.info.domain.repository.AccountInformationRepository
import com.algorand.android.module.account.info.domain.usecase.InitializeAccountInformation
import javax.inject.Inject

internal class InitializeAccountInformationUseCase @Inject constructor(
    private val accountInformationRepository: AccountInformationRepository
) : InitializeAccountInformation {

    override suspend fun invoke(addresses: List<String>): Map<String, List<Long>> {
        accountInformationRepository.clearCache()
        return mutableMapOf<String, List<Long>>().apply {
            accountInformationRepository.fetchAndCacheAccountInformation(addresses).mapNotNull {
                put(it.key, it.value?.assetHoldings?.map { it.assetId }.orEmpty())
            }
        }
    }
}
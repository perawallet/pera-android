package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import javax.inject.Inject

internal class IsAccountOptedInToAnyAppUseCase @Inject constructor(
    private val accountInformationRepository: AccountInformationRepository
): IsAccountOptedInToAnyApp {

    override suspend fun invoke(address: String): Boolean {
        val assetsAndAppsCount = accountInformationRepository.getAccountAssetsAndAppsCount(address) ?: return false
        return assetsAndAppsCount.optedInAppsCount > 0 || assetsAndAppsCount.totalCreatedAppsCount > 0
    }
}

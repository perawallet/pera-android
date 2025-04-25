package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import javax.inject.Inject

internal class IsAccountOptedInToAnyAssetUseCase @Inject constructor(
    private val accountInformationRepository: AccountInformationRepository
) : IsAccountOptedInToAnyAsset {

    override suspend fun invoke(address: String): Boolean {
        val assetAndAppsCount = accountInformationRepository.getAccountAssetsAndAppsCount(address) ?: return false
        return assetAndAppsCount.optedInAssetsCount > 0 || assetAndAppsCount.totalCreatedAssetsCount > 0
    }
}

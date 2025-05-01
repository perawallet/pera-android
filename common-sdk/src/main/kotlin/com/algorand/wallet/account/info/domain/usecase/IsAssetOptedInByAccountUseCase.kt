package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject

internal class IsAssetOptedInByAccountUseCase @Inject constructor(
    private val accountInformationRepository: AccountInformationRepository
) : IsAssetOptedInByAccount {

    override suspend fun invoke(address: String, assetId: Long): Boolean {
        return assetId == ALGO_ID || accountInformationRepository.isAssetOptedInByAccount(address, assetId)
    }
}

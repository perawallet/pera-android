package com.algorand.wallet.account.info.data.mapper.model

import com.algorand.wallet.account.info.data.model.AccountAssetAndAppsCountDto
import com.algorand.wallet.account.info.domain.model.AccountAssetAndAppsCount
import javax.inject.Inject

internal class AccountAssetAndAppsCountMapperImpl @Inject constructor() : AccountAssetAndAppsCountMapper {

    override fun map(dto: AccountAssetAndAppsCountDto): AccountAssetAndAppsCount {
        return with(dto) {
            AccountAssetAndAppsCount(
                optedInAssetsCount = optedInAssetsCount,
                optedInAppsCount = optedInAppsCount,
                totalCreatedAssetsCount = totalCreatedAssetsCount,
                totalCreatedAppsCount = totalCreatedAppsCount
            )
        }
    }
}

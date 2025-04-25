package com.algorand.wallet.account.info.data.mapper.model

import com.algorand.wallet.account.info.data.model.AccountAssetAndAppsCountDto
import com.algorand.wallet.account.info.domain.model.AccountAssetAndAppsCount

internal interface AccountAssetAndAppsCountMapper {
    fun map(dto: AccountAssetAndAppsCountDto): AccountAssetAndAppsCount
}

package com.algorand.android.module.account.info.domain.mapper

import com.algorand.android.module.account.info.domain.model.*

internal interface AccountAssetsMapper {
    operator fun invoke(address: String, assetHoldings: List<AssetHolding>): AccountAssets
}

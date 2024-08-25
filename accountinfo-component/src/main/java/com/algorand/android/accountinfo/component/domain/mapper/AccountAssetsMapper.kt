package com.algorand.android.accountinfo.component.domain.mapper

import com.algorand.android.accountinfo.component.domain.model.*

internal interface AccountAssetsMapper {
    operator fun invoke(address: String, assetHoldings: List<AssetHolding>): AccountAssets
}

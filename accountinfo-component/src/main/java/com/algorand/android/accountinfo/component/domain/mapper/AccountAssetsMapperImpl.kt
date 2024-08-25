package com.algorand.android.accountinfo.component.domain.mapper

import com.algorand.android.accountinfo.component.domain.model.*
import javax.inject.Inject

internal class AccountAssetsMapperImpl @Inject constructor() : AccountAssetsMapper {

    override fun invoke(address: String, assetHoldings: List<AssetHolding>): AccountAssets {
        return AccountAssets(
            address = address,
            assetHoldings = assetHoldings
        )
    }
}

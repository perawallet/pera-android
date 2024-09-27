package com.algorand.android.module.account.info.domain.mapper

import com.algorand.android.module.account.info.domain.model.AccountAssets
import com.algorand.android.module.account.info.domain.model.AssetHolding
import javax.inject.Inject

internal class AccountAssetsMapperImpl @Inject constructor() : AccountAssetsMapper {

    override fun invoke(address: String, assetHoldings: List<AssetHolding>): AccountAssets {
        return AccountAssets(
            address = address,
            assetHoldings = assetHoldings
        )
    }
}

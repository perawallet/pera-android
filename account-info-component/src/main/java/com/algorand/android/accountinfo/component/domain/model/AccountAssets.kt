package com.algorand.android.accountinfo.component.domain.model

internal data class AccountAssets(
    val address: String,
    val assetHoldings: List<AssetHolding>
)

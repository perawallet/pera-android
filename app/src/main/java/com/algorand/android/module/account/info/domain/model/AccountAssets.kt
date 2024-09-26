package com.algorand.android.module.account.info.domain.model

internal data class AccountAssets(
    val address: String,
    val assetHoldings: List<AssetHolding>
)

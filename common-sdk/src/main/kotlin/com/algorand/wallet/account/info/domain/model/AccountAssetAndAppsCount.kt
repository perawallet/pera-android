package com.algorand.wallet.account.info.domain.model

data class AccountAssetAndAppsCount(
    val optedInAppsCount: Int,
    val optedInAssetsCount: Int,
    val totalCreatedAppsCount: Int,
    val totalCreatedAssetsCount: Int
)

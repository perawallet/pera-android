package com.algorand.android.module.account.info.domain.model

import java.math.BigInteger

data class AccountInformation(
    val address: String,
    val amount: BigInteger,
    val lastFetchedRound: Long,
    val rekeyAdminAddress: String?,
    val totalAppsOptedIn: Int,
    val totalAssetsOptedIn: Int,
    val totalCreatedApps: Int,
    val totalCreatedAssets: Int,
    val appsTotalExtraPages: Int,
    val appsTotalSchema: AppStateScheme?,
    val assetHoldings: List<AssetHolding>,
    val createdAtRound: Long?
) {

    fun isRekeyed(): Boolean {
        return !rekeyAdminAddress.isNullOrEmpty() && rekeyAdminAddress != address
    }

    fun hasAsset(assetId: Long): Boolean {
        return assetHoldings.any { it.assetId == assetId }
    }

    fun hasAssetAmount(assetId: Long): Boolean {
        return assetHoldings.any { it.assetId == assetId && it.amount > BigInteger.ZERO }
    }

    fun getAssetHoldingIds() = assetHoldings.map { it.assetId }

    fun isCreated() = createdAtRound != null
}

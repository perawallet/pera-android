package com.algorand.wallet.account.info.data.model

import androidx.room.ColumnInfo

data class AccountAssetAndAppsCountDto(
    @ColumnInfo(name = "opted_in_apps_count")
    val optedInAppsCount: Int,

    @ColumnInfo(name = "opted_in_assets_count")
    val optedInAssetsCount: Int,

    @ColumnInfo(name = "total_created_apps_count")
    val totalCreatedAppsCount: Int,

    @ColumnInfo(name = "total_created_assets_count")
    val totalCreatedAssetsCount: Int
)

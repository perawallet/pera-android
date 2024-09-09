package com.algorand.android.accountinfo.component.domain.usecase

interface IsAssetOwnedByAccount {
    suspend operator fun invoke(address: String, assetId: Long): Boolean
}

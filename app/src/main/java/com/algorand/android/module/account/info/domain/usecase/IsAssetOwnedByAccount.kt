package com.algorand.android.module.account.info.domain.usecase

interface IsAssetOwnedByAccount {
    suspend operator fun invoke(address: String, assetId: Long): Boolean
}

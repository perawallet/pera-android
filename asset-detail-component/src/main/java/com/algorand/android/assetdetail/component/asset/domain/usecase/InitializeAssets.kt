package com.algorand.android.assetdetail.component.asset.domain.usecase

interface InitializeAssets {
    suspend operator fun invoke(assetIds: List<Long>)
}

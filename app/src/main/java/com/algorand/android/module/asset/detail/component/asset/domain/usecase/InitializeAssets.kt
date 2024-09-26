package com.algorand.android.module.asset.detail.component.asset.domain.usecase

interface InitializeAssets {
    suspend operator fun invoke(assetIds: List<Long>)
}

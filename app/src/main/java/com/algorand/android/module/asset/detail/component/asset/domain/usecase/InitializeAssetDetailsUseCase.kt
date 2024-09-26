package com.algorand.android.module.asset.detail.component.asset.domain.usecase

import com.algorand.android.module.asset.detail.component.asset.domain.repository.AssetRepository
import javax.inject.Inject

internal class InitializeAssetDetailsUseCase @Inject constructor(
    private val assetRepository: AssetRepository
) : InitializeAssets {

    override suspend fun invoke(assetIds: List<Long>) {
        assetRepository.clearCache()
        assetRepository.fetchAndCacheAssets(assetIds.toList(), includeDeleted = false)
    }
}

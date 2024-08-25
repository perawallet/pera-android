package com.algorand.android.assetdetail.component.asset.domain.usecase

import com.algorand.android.assetdetail.component.asset.domain.repository.AssetRepository
import javax.inject.Inject

internal class InitializeAssetDetailsUseCase @Inject constructor(
    private val assetRepository: AssetRepository
) : InitializeAssets {

    override suspend fun invoke(assetIds: List<Long>) {
        assetRepository.clearCache()
        assetRepository.fetchAndCacheAssets(assetIds.toList(), includeDeleted = false)
    }
}

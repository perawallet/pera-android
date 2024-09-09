package com.algorand.android.assetdetail.component.collectible.domain.repository

import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.foundation.PeraResult

internal interface CollectibleDetailRepository {
    suspend fun fetchCollectibleDetail(collectibleAssetId: Long): PeraResult<CollectibleDetail>
}

package com.algorand.android.module.asset.detail.component.collectible.domain.repository

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.foundation.PeraResult

internal interface CollectibleDetailRepository {
    suspend fun fetchCollectibleDetail(collectibleAssetId: Long): PeraResult<CollectibleDetail>
}

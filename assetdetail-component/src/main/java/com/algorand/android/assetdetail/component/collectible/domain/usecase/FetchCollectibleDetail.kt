package com.algorand.android.assetdetail.component.collectible.domain.usecase

import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.foundation.PeraResult

fun interface FetchCollectibleDetail {
    suspend operator fun invoke(collectibleId: Long): PeraResult<CollectibleDetail>
}

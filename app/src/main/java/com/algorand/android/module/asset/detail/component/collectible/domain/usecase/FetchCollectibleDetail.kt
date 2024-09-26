package com.algorand.android.module.asset.detail.component.collectible.domain.usecase

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.foundation.PeraResult

fun interface FetchCollectibleDetail {
    suspend operator fun invoke(collectibleId: Long): PeraResult<CollectibleDetail>
}

package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleSearchResponse
import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleSearch

internal interface CollectibleSearchMapper {
    operator fun invoke(response: CollectibleSearchResponse): CollectibleSearch
}

package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleSearchResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleSearch

internal interface CollectibleSearchMapper {
    operator fun invoke(response: CollectibleSearchResponse): CollectibleSearch
}

package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleSearchResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleSearch
import javax.inject.Inject

internal class CollectibleSearchMapperImpl @Inject constructor() : CollectibleSearchMapper {

    override fun invoke(response: CollectibleSearchResponse): CollectibleSearch {
        return CollectibleSearch(
            primaryImageUrl = response.primaryImageUrl,
            title = response.title,
        )
    }
}

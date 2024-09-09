package com.algorand.android.assetdetail.component.asset.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleSearchResponse
import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleSearch
import javax.inject.Inject

internal class CollectibleSearchMapperImpl @Inject constructor() : CollectibleSearchMapper {

    override fun invoke(response: CollectibleSearchResponse): CollectibleSearch {
        return CollectibleSearch(
            primaryImageUrl = response.primaryImageUrl,
            title = response.title,
        )
    }
}

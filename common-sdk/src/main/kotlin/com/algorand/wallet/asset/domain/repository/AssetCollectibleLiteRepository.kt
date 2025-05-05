package com.algorand.wallet.asset.domain.repository

import androidx.paging.PagingData
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetLite
import kotlinx.coroutines.flow.Flow

internal interface AssetCollectibleLiteRepository {
    fun getPaginatedAssetCollectibleLiteItems(query: AssetCollectibleLiteQuery): Flow<PagingData<AssetLite>>
}

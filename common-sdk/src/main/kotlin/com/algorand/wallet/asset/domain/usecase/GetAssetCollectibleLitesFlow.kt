package com.algorand.wallet.asset.domain.usecase

import androidx.paging.PagingData
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetLite
import kotlinx.coroutines.flow.Flow

fun interface GetAssetCollectibleLitesFlow {
    operator fun invoke(query: AssetCollectibleLiteQuery): Flow<PagingData<AssetLite>>
}

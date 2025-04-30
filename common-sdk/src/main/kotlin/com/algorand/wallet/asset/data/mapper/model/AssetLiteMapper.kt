package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import com.algorand.wallet.asset.domain.model.AssetLite

internal fun interface AssetLiteMapper {
    operator fun invoke(dto: PaginatedAssetCollectibleItemDto): AssetLite
}

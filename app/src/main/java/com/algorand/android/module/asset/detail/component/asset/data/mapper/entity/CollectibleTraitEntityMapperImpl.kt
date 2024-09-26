package com.algorand.android.module.asset.detail.component.asset.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity
import javax.inject.Inject

internal class CollectibleTraitEntityMapperImpl @Inject constructor() : CollectibleTraitEntityMapper {

    override fun invoke(response: AssetResponse): List<CollectibleTraitEntity> {
        return response.collectible?.traits?.mapNotNull {
            CollectibleTraitEntity(
                collectibleAssetId = response.assetId ?: return@mapNotNull null,
                displayValue = it.value,
                displayName = it.name
            )
        }.orEmpty()
    }
}

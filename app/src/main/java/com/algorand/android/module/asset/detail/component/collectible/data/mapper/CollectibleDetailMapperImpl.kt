package com.algorand.android.module.asset.detail.component.collectible.data.mapper

import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.AUDIO
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.IMAGE
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.MIXED
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.UNKNOWN
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse.VIDEO
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.shareddb.assetdetail.model.AssetDetailEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity
import javax.inject.Inject

internal class CollectibleDetailMapperImpl @Inject constructor(
    private val imageCollectibleDetailMapper: ImageCollectibleDetailMapper,
    private val mixedCollectibleDetailMapper: MixedCollectibleDetailMapper,
    private val videoCollectibleDetailMapper: VideoCollectibleDetailMapper,
    private val audioCollectibleDetailMapper: AudioCollectibleDetailMapper,
    private val unsupportedCollectibleDetailMapper: UnsupportedCollectibleDetailMapper
) : CollectibleDetailMapper {

    override fun invoke(response: AssetResponse): CollectibleDetail? {
        val collectible = response.collectible ?: return null
        return when (response.collectible.mediaType) {
            IMAGE -> imageCollectibleDetailMapper(response, collectible)
            VIDEO -> videoCollectibleDetailMapper(response, collectible)
            MIXED -> mixedCollectibleDetailMapper(response, collectible)
            UNKNOWN -> unsupportedCollectibleDetailMapper(response, collectible)
            AUDIO -> audioCollectibleDetailMapper(response, collectible)
            else -> null
        }
    }

    override fun invoke(
        entity: AssetDetailEntity,
        collectibleEntity: CollectibleEntity,
        mediaEntities: List<CollectibleMediaEntity>?,
        traitEntities: List<CollectibleTraitEntity>?
    ): CollectibleDetail? {
        return when (collectibleEntity.mediaType) {
            CollectibleMediaTypeEntity.IMAGE -> {
                imageCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.VIDEO -> {
                videoCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.MIXED -> {
                mixedCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.AUDIO -> {
                audioCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            CollectibleMediaTypeEntity.UNKNOWN -> {
                unsupportedCollectibleDetailMapper(entity, collectibleEntity, mediaEntities, traitEntities)
            }
            null -> null
        }
    }
}

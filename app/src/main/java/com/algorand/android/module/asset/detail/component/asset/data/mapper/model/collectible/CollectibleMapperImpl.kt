package com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.Collectible
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity
import javax.inject.Inject

internal class CollectibleMapperImpl @Inject constructor(
    private val collectibleStandardTypeMapper: CollectibleStandardTypeMapper,
    private val collectibleMediaTypeMapper: CollectibleMediaTypeMapper,
    private val collectionMapper: CollectionMapper,
    private val collectibleMediaMapper: CollectibleMediaMapper,
    private val collectibleTraitMapper: CollectibleTraitMapper
) : CollectibleMapper {

    override fun invoke(response: CollectibleResponse): Collectible? {
        if (!response.isValid()) return null
        return Collectible(
            standardType = response.standard?.let { collectibleStandardTypeMapper(it) },
            mediaType = response.mediaType?.let { collectibleMediaTypeMapper(it) },
            primaryImageUrl = response.primaryImageUrl,
            title = response.title,
            collection = response.collection?.let { collectionMapper(it) },
            collectibleMedias = response.collectibleMedias?.mapNotNull { collectibleMediaMapper(it) }.orEmpty(),
            description = response.description,
            traits = response.traits?.mapNotNull { collectibleTraitMapper(it) }.orEmpty()
        )
    }

    override fun invoke(
        entity: CollectibleEntity,
        collectibleMediaEntities: List<CollectibleMediaEntity>?,
        traitEntities: List<CollectibleTraitEntity>?
    ): Collectible {
        return with(entity) {
            Collectible(
                standardType = standardType?.let { collectibleStandardTypeMapper(it) },
                mediaType = mediaType?.let { collectibleMediaTypeMapper(it) },
                primaryImageUrl = primaryImageUrl,
                title = title,
                collection = collectionMapper(collectionId, collectionName, collectionDescription),
                collectibleMedias = collectibleMediaEntities?.let { collectibleMediaMapper(it) }.orEmpty(),
                description = description,
                traits = traitEntities?.let { collectibleTraitMapper(it) }.orEmpty()
            )
        }
    }

    private fun CollectibleResponse.isValid(): Boolean {
        return standard != null || mediaType != null || primaryImageUrl != null || title != null ||
            collection != null || !collectibleMedias.isNullOrEmpty()
    }
}

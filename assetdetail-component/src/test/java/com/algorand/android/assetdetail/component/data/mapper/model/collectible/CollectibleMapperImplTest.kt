package com.algorand.android.assetdetail.component.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.data.model.collectible.*
import com.algorand.android.assetdetail.component.domain.model.*
import com.algorand.android.shared_db.assetdetail.model.*
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class CollectibleMapperImplTest {

    private val collectibleStandardTypeMapper: CollectibleStandardTypeMapper = mock {
        on { invoke(CollectibleStandardTypeResponse.ARC_3) } doReturn CollectibleStandardType.ARC_3
    }
    private val collectibleMediaTypeMapper: CollectibleMediaTypeMapper = mock {
        on { invoke(CollectibleMediaTypeResponse.IMAGE) } doReturn CollectibleMediaType.IMAGE
    }
    private val collectionMapper: CollectionMapper = mock {
        on { invoke(COLLECTION_RESPONSE) } doReturn COLLECTION
    }
    private val collectibleMediaMapper: CollectibleMediaMapper = mock {
        on { invoke(COLLECTIBLE_MEDIA_RESPONSE) } doReturn COLLECTIBLE_MEDIA
    }
    private val collectibleTraitMapper: CollectibleTraitMapper = mock {
        on { invoke(COLLECTIBLE_TRAIT_RESPONSE) } doReturn COLLECTIBLE_TRAIT
    }

    private val sut = CollectibleMapperImpl(
        collectibleStandardTypeMapper,
        collectibleMediaTypeMapper,
        collectionMapper,
        collectibleMediaMapper,
        collectibleTraitMapper
    )

    @Test
    fun `EXPECT response to be mapped successfully WHEN fields are valid`() {
        val result = sut(COLLECTIBLE_RESPONSE)

        assertEquals(COLLECTIBLE, result)
    }

    @Test
    fun `EXPECT null WHEN response fields are null`() {
        whenever(collectibleStandardTypeMapper(CollectibleStandardTypeEntity.ARC_3))
            .thenReturn(CollectibleStandardType.ARC_3)
        whenever(collectibleMediaTypeMapper(CollectibleMediaTypeEntity.IMAGE))
            .thenReturn(CollectibleMediaType.IMAGE)
        val collectibleResponse = CollectibleResponse(
            standard = null,
            mediaType = null,
            primaryImageUrl = null,
            title = null,
            collection = null,
            collectibleMedias = emptyList(),
            description = null,
            traits = emptyList()
        )

        val result = sut(collectibleResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT entity to be mapped successfully`() {
        whenever(collectionMapper(123L, "collectionName", "collectionDescription")).thenReturn(COLLECTION)
        whenever(collectibleMediaMapper.invoke(listOf(COLLECTIBLE_MEDIA_ENTITY))).thenReturn(listOf(COLLECTIBLE_MEDIA))
        whenever(collectibleTraitMapper.invoke(listOf(COLLECTIBLE_TRAIT_ENTITY))).thenReturn(listOf(COLLECTIBLE_TRAIT))
        whenever(collectibleMediaTypeMapper(CollectibleMediaTypeEntity.IMAGE)).thenReturn(CollectibleMediaType.IMAGE)
        whenever(collectibleStandardTypeMapper(CollectibleStandardTypeEntity.ARC_3))
            .thenReturn(CollectibleStandardType.ARC_3)

        val result = sut(
            COLLECTIBLE_ENTITY,
            listOf(COLLECTIBLE_MEDIA_ENTITY),
            listOf(COLLECTIBLE_TRAIT_ENTITY)
        )

        assertEquals(COLLECTIBLE, result)
    }

    companion object {
        private val COLLECTIBLE_MEDIA_RESPONSE = CollectibleMediaResponse(
            mediaType = CollectibleMediaTypeResponse.IMAGE,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = CollectibleMediaTypeExtensionResponse.GIF
        )
        private val COLLECTIBLE_MEDIA = CollectibleMedia(
            mediaType = CollectibleMediaType.IMAGE,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = CollectibleMediaTypeExtension.GIF
        )
        private val COLLECTIBLE_TRAIT_RESPONSE = CollectibleTraitResponse(
            name = "name",
            value = "value"
        )
        private val COLLECTIBLE_TRAIT = CollectibleTrait(
            name = "name",
            value = "value"
        )
        private val COLLECTION_RESPONSE = CollectionResponse(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )
        private val COLLECTION = Collection(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )
        private val COLLECTIBLE_RESPONSE = CollectibleResponse(
            standard = CollectibleStandardTypeResponse.ARC_3,
            mediaType = CollectibleMediaTypeResponse.IMAGE,
            primaryImageUrl = "primaryImageUrl",
            title = "title",
            collection = COLLECTION_RESPONSE,
            collectibleMedias = listOf(COLLECTIBLE_MEDIA_RESPONSE),
            description = "description",
            traits = listOf(COLLECTIBLE_TRAIT_RESPONSE)
        )

        private val COLLECTIBLE = Collectible(
            standardType = CollectibleStandardType.ARC_3,
            mediaType = CollectibleMediaType.IMAGE,
            primaryImageUrl = "primaryImageUrl",
            title = "title",
            collection = COLLECTION,
            collectibleMedias = listOf(COLLECTIBLE_MEDIA),
            description = "description",
            traits = listOf(COLLECTIBLE_TRAIT)
        )

        private val COLLECTIBLE_ENTITY = CollectibleEntity(
            id = 1L,
            collectibleAssetId = 1L,
            standardType = CollectibleStandardTypeEntity.ARC_3,
            mediaType = CollectibleMediaTypeEntity.IMAGE,
            primaryImageUrl = "primaryImageUrl",
            title = "title",
            collectionId = 123L,
            collectionName = COLLECTION.collectionName,
            collectionDescription = COLLECTION.collectionDescription,
            description = "description"
        )

        private val COLLECTIBLE_MEDIA_ENTITY = CollectibleMediaEntity(
            id = 2L,
            collectibleAssetId = 1L,
            mediaType = CollectibleMediaTypeEntity.IMAGE,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = CollectibleMediaTypeExtensionEntity.GIF
        )
        private val COLLECTIBLE_TRAIT_ENTITY = CollectibleTraitEntity(
            id = 3L,
            collectibleAssetId = 1L,
            displayName = "name",
            displayValue = "value"
        )
    }
}

/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.asset.data.mapper.model.collectible

import com.algorand.common.asset.data.database.model.CollectibleEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaTypeEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaTypeExtensionEntity
import com.algorand.common.asset.data.database.model.CollectibleStandardTypeEntity
import com.algorand.common.asset.data.database.model.CollectibleTraitEntity
import com.algorand.common.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import com.algorand.common.asset.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.common.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.common.asset.data.model.collectible.CollectionResponse
import com.algorand.common.asset.domain.model.Collectible
import com.algorand.common.asset.domain.model.CollectibleMedia
import com.algorand.common.asset.domain.model.CollectibleMediaType
import com.algorand.common.asset.domain.model.CollectibleMediaTypeExtension
import com.algorand.common.asset.domain.model.CollectibleStandardType.ARC_3
import com.algorand.common.asset.domain.model.CollectibleTrait
import com.algorand.common.asset.domain.model.Collection
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class CollectibleMapperImplTest {

    private val collectibleStandardTypeMapper: CollectibleStandardTypeMapper = mockk {
        every { invoke(response = CollectibleStandardTypeResponse.ARC_3) } returns ARC_3
    }
    private val collectibleMediaTypeMapper: CollectibleMediaTypeMapper = mockk {
        every { invoke(response = CollectibleMediaTypeResponse.IMAGE) } returns CollectibleMediaType.IMAGE
    }
    private val collectionMapper: CollectionMapper = mockk {
        every { invoke(response = COLLECTION_RESPONSE) } returns COLLECTION
    }
    private val collectibleMediaMapper: CollectibleMediaMapper = mockk {
        every { invoke(response = COLLECTIBLE_MEDIA_RESPONSE) } returns COLLECTIBLE_MEDIA
    }
    private val collectibleTraitMapper: CollectibleTraitMapper = mockk {
        every { invoke(response = COLLECTIBLE_TRAIT_RESPONSE) } returns COLLECTIBLE_TRAIT
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
        every { collectibleStandardTypeMapper(CollectibleStandardTypeEntity.ARC_3) } returns ARC_3
        every { collectibleMediaTypeMapper(CollectibleMediaTypeEntity.IMAGE) } returns CollectibleMediaType.IMAGE

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
        every { collectionMapper(123, "collectionName", "collectionDescription") } returns COLLECTION
        every { collectibleMediaMapper(entities = listOf(COLLECTIBLE_MEDIA_ENTITY)) } returns listOf(COLLECTIBLE_MEDIA)
        every { collectibleMediaTypeMapper(CollectibleMediaTypeEntity.IMAGE) } returns CollectibleMediaType.IMAGE
        every { collectibleStandardTypeMapper(CollectibleStandardTypeEntity.ARC_3) } returns ARC_3
        every { collectibleTraitMapper(entities = listOf(COLLECTIBLE_TRAIT_ENTITY)) } returns listOf(COLLECTIBLE_TRAIT)

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
            standardType = ARC_3,
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

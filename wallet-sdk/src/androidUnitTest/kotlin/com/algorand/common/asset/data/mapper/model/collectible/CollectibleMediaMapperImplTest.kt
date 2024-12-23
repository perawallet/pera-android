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

import com.algorand.common.asset.data.database.model.CollectibleMediaEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaTypeEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaTypeExtensionEntity
import com.algorand.common.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.common.asset.domain.model.CollectibleMedia
import com.algorand.common.asset.domain.model.CollectibleMediaType
import com.algorand.common.asset.domain.model.CollectibleMediaType.AUDIO
import com.algorand.common.asset.domain.model.CollectibleMediaTypeExtension
import com.algorand.common.asset.domain.model.CollectibleMediaTypeExtension.GIF
import com.algorand.common.testing.peraFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class CollectibleMediaMapperImplTest {

    private val collectibleMediaTypeMapper: CollectibleMediaTypeMapper = mockk {
        every { invoke(response = COLLECTIBLE_MEDIA_TYPE_RESPONSE) } returns COLLECTIBLE_MEDIA_TYPE
    }
    private val collectibleMediaTypeExtensionMapper: CollectibleMediaTypeExtensionMapper = mockk {
        every { invoke(response = COLLECTIBLE_MEDIA_TYPE_EXTENSION_RESPONSE) } returns COLLECTIBLE_MEDIA_TYPE_EXTENSION
    }

    private val sut = CollectibleMediaMapperImpl(
        collectibleMediaTypeMapper,
        collectibleMediaTypeExtensionMapper
    )

    @Test
    fun `EXPECT collectible media WHEN response fields are valid`() {
        val result = sut(COLLECTIBLE_MEDIA_RESPONSE)

        assertEquals(COLLECTIBLE_MEDIA, result)
    }

    @Test
    fun `EXPECT null WHEN all response fields are null`() {
        val collectibleMediaResponse = CollectibleMediaResponse(
            mediaType = null,
            downloadUrl = null,
            previewUrl = null,
            mediaTypeExtension = null
        )

        val result = sut(collectibleMediaResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT collectible media list WHEN entity list is valid`() {
        every { collectibleMediaTypeExtensionMapper(CollectibleMediaTypeExtensionEntity.GIF) } returns GIF
        every { collectibleMediaTypeMapper(CollectibleMediaTypeEntity.AUDIO) } returns AUDIO

        val collectibleMediaEntity = CollectibleMediaEntity(
            id = 2L,
            collectibleAssetId = 1L,
            mediaType = CollectibleMediaTypeEntity.AUDIO,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = CollectibleMediaTypeExtensionEntity.GIF
        )
        val collectibleMediaEntities = listOf(collectibleMediaEntity)

        val result = sut(collectibleMediaEntities)

        val expected = CollectibleMedia(
            mediaType = AUDIO,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = GIF
        )
        assertEquals(listOf(expected), result)
    }

    companion object {
        private val COLLECTIBLE_MEDIA_TYPE = peraFixture<CollectibleMediaType>()
        private val COLLECTIBLE_MEDIA_TYPE_RESPONSE = peraFixture<CollectibleMediaTypeResponse>()
        private val COLLECTIBLE_MEDIA_TYPE_EXTENSION = peraFixture<CollectibleMediaTypeExtension>()
        private val COLLECTIBLE_MEDIA_TYPE_EXTENSION_RESPONSE = peraFixture<CollectibleMediaTypeExtensionResponse>()

        private val COLLECTIBLE_MEDIA = CollectibleMedia(
            mediaType = COLLECTIBLE_MEDIA_TYPE,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = COLLECTIBLE_MEDIA_TYPE_EXTENSION
        )

        private val COLLECTIBLE_MEDIA_RESPONSE = CollectibleMediaResponse(
            mediaType = COLLECTIBLE_MEDIA_TYPE_RESPONSE,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = COLLECTIBLE_MEDIA_TYPE_EXTENSION_RESPONSE
        )
    }
}

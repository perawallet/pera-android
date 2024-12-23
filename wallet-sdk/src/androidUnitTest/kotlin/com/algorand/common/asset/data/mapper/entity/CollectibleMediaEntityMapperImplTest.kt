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

package com.algorand.common.asset.data.mapper.entity

import com.algorand.common.asset.data.database.model.CollectibleMediaEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaTypeEntity.IMAGE
import com.algorand.common.asset.data.database.model.CollectibleMediaTypeExtensionEntity.GIF
import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.common.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import com.algorand.common.testing.peraFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CollectibleMediaEntityMapperImplTest {

    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper = mockk()
    private val collectibleMediaTypeExtensionEntityMapper: CollectibleMediaTypeExtensionEntityMapper = mockk()

    private val sut = CollectibleMediaEntityMapperImpl(
        collectibleMediaTypeEntityMapper,
        collectibleMediaTypeExtensionEntityMapper,
    )

    @Test
    fun `EXPECT null WHEN asset id is null`() {
        val assetDetailResponse = peraFixture<AssetResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val mediaTypeResponse = peraFixture<CollectibleMediaTypeResponse>()
        val mediaTypeExtensionResponse = peraFixture<CollectibleMediaTypeExtensionResponse>()
        val collectibleMediaResponse = peraFixture<CollectibleMediaResponse>().copy(
            mediaType = mediaTypeResponse,
            mediaTypeExtension = mediaTypeExtensionResponse
        )
        val collectibleResponse = peraFixture<CollectibleResponse>().copy(
            collectibleMedias = listOf(collectibleMediaResponse)
        )
        val assetDetailResponse = peraFixture<AssetResponse>().copy(
            assetId = 1L,
            collectible = collectibleResponse
        )
        every { collectibleMediaTypeEntityMapper(mediaTypeResponse) } returns IMAGE
        every { collectibleMediaTypeExtensionEntityMapper(mediaTypeExtensionResponse) } returns GIF

        val result = sut(assetDetailResponse)

        val expected = listOf(
            CollectibleMediaEntity(
                collectibleAssetId = 1L,
                mediaType = IMAGE,
                downloadUrl = collectibleMediaResponse.downloadUrl,
                previewUrl = collectibleMediaResponse.previewUrl,
                mediaTypeExtension = GIF
            )
        )
        assertEquals(expected, result)
    }
}

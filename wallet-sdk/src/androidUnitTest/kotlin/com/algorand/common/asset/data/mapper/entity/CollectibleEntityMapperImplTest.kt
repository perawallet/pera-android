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

import com.algorand.common.asset.data.database.model.CollectibleEntity
import com.algorand.common.asset.data.database.model.CollectibleMediaTypeEntity.MIXED
import com.algorand.common.asset.data.database.model.CollectibleStandardTypeEntity.ARC_3
import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import com.algorand.common.testing.peraFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleEntityMapperImplTest {

    private val collectibleStandardEntityMapper: CollectibleStandardTypeEntityMapper = mockk()
    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper = mockk()

    private val sut = CollectibleEntityMapperImpl(
        collectibleStandardEntityMapper,
        collectibleMediaTypeEntityMapper
    )

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val collectibleResponse = peraFixture<CollectibleResponse>()
        val assetDetailResponse = peraFixture<AssetResponse>().copy(
            assetId = 1L,
            collectible = collectibleResponse
        )
        every { collectibleStandardEntityMapper(collectibleResponse.standard) } returns ARC_3
        every { collectibleMediaTypeEntityMapper(collectibleResponse.mediaType) } returns MIXED

        val result = sut(assetDetailResponse)

        val expected = CollectibleEntity(
            collectibleAssetId = 1L,
            standardType = ARC_3,
            mediaType = MIXED,
            primaryImageUrl = collectibleResponse.primaryImageUrl,
            title = collectibleResponse.title,
            description = collectibleResponse.description,
            collectionId = collectibleResponse.collection?.collectionId,
            collectionName = collectibleResponse.collection?.collectionName,
            collectionDescription = collectibleResponse.collection?.collectionDescription
        )
        assertEquals(expected, result)
    }
}

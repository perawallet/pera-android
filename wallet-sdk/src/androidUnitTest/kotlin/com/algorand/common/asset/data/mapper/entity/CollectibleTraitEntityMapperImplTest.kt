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

import com.algorand.common.asset.data.database.model.CollectibleTraitEntity
import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.asset.data.model.collectible.CollectibleResponse
import com.algorand.common.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.common.testing.peraFixture
import org.junit.Assert
import org.junit.Test

internal class CollectibleTraitEntityMapperImplTest {

    private val sut = CollectibleTraitEntityMapperImpl()

    @Test
    fun `EXPECT null WHEN asset id is null`() {
        val assetDetailResponse = peraFixture<AssetResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT entity WHEN traits exists in response`() {
        val assetDetailResponse = peraFixture<AssetResponse>().copy(
            assetId = 1,
            collectible = peraFixture<CollectibleResponse>().copy(
                traits = listOf(
                    CollectibleTraitResponse(name = "name1", value = "value1"),
                    CollectibleTraitResponse(name = "name2", value = "value2")
                )
            )
        )

        val result = sut(assetDetailResponse)

        val expected = listOf(
            CollectibleTraitEntity(collectibleAssetId = 1, displayName = "name1", displayValue = "value1"),
            CollectibleTraitEntity(collectibleAssetId = 1, displayName = "name2", displayValue = "value2")
        )
        Assert.assertEquals(expected, result)
    }
}

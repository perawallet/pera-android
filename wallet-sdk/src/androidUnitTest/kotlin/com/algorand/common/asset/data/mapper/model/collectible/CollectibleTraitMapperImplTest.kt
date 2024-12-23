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

import com.algorand.common.asset.data.database.model.CollectibleTraitEntity
import com.algorand.common.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.common.asset.domain.model.CollectibleTrait
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class CollectibleTraitMapperImplTest {

    private val sut = CollectibleTraitMapperImpl()

    @Test
    fun `EXPECT collectible trait WHEN response fields are valid`() {
        val response = CollectibleTraitResponse(name = "traitName", value = "value")

        val result = sut(response)

        val expected = CollectibleTrait(name = "traitName", value = "value")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN response fields are null`() {
        val response = CollectibleTraitResponse(name = null, value = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT collectible trait list WHEN entity list is valid`() {
        val entityList = listOf(
            CollectibleTraitEntity(
                displayName = "traitName1",
                displayValue = "value1",
                id = 1L,
                collectibleAssetId = 2L
            ),
            CollectibleTraitEntity(
                displayName = "traitName2",
                displayValue = "value2",
                id = 2L,
                collectibleAssetId = 2L
            )
        )

        val result = sut(entityList)

        val expectedList = listOf(
            CollectibleTrait(name = "traitName1", value = "value1"),
            CollectibleTrait(name = "traitName2", value = "value2")
        )
        assertEquals(expectedList, result)
    }
}

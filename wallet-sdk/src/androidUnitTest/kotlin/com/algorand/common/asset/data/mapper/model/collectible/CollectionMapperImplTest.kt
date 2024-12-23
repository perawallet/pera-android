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

import com.algorand.common.asset.data.model.collectible.CollectionResponse
import com.algorand.common.asset.domain.model.Collection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class CollectionMapperImplTest {

    private val sut = CollectionMapperImpl()

    @Test
    fun `EXPECT collection WHEN response fields are valid`() {
        val response = CollectionResponse(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )

        val result = sut(response)

        val expected = Collection(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all response fields are null`() {
        val response = CollectionResponse(
            collectionId = null,
            collectionName = null,
            collectionDescription = null
        )

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT collection WHEN at least one response field is valid`() {
        val response = CollectionResponse(
            collectionId = 123L,
            collectionName = null,
            collectionDescription = null
        )

        val result = sut(response)

        val expected = Collection(
            collectionId = 123L,
            collectionName = null,
            collectionDescription = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT collection WHEN parameters are valid`() {
        val result = sut(123L, "collectionName", "collectionDescription")

        val expected = Collection(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all parameters are null`() {
        val result = sut(null, null, null)

        assertNull(result)
    }

    @Test
    fun `EXPECT collection WHEN at least one parameter is valid`() {
        val result = sut(123L, null, null)

        val expected = Collection(
            collectionId = 123L,
            collectionName = null,
            collectionDescription = null
        )
        assertEquals(expected, result)
    }
}

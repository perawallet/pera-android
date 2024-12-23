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

package com.algorand.common.asset.data.mapper.model

import com.algorand.common.asset.data.model.AssetCreatorResponse
import com.algorand.common.asset.domain.model.AssetCreator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class AssetCreatorMapperImplTest {

    private val sut = AssetCreatorMapperImpl()

    @Test
    fun `EXPECT asset creator WHEN response fields are valid`() {
        val response = AssetCreatorResponse(publicKey = "address", id = 10L, isVerifiedAssetCreator = false)

        val result = sut(response)

        val expected = AssetCreator(publicKey = "address", id = 10L, isVerifiedAssetCreator = false)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT assert creator WHEN at least one response field is not null`() {
        val response = AssetCreatorResponse(publicKey = "address", id = null, isVerifiedAssetCreator = null)

        val result = sut(response)

        val expected = AssetCreator(publicKey = "address", id = null, isVerifiedAssetCreator = null)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all response fields are null`() {
        val response = AssetCreatorResponse(publicKey = null, id = null, isVerifiedAssetCreator = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT asset creator WHEN parameters are valid`() {
        val result = sut(10L, "address", false)

        val expected = AssetCreator(publicKey = "address", id = 10L, isVerifiedAssetCreator = false)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all parameters are null`() {
        val result = sut(null, null, null)

        assertNull(result)
    }

    @Test
    fun `EXPECT assert creator WHEN at least one parameter is not null`() {
        val result = sut(null, "address", null)

        val expected = AssetCreator(publicKey = "address", id = null, isVerifiedAssetCreator = null)
        assertEquals(expected, result)
    }
}

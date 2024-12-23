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

import com.algorand.common.asset.data.model.collectible.CollectibleSearchResponse
import com.algorand.common.asset.domain.model.CollectibleSearch
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleSearchMapperImplTest {

    private val sut = CollectibleSearchMapperImpl()

    @Test
    fun `EXPECT response to be mapped successfully`() {
        val collectibleSearchResponse = CollectibleSearchResponse("primaryImageUrl", "title")

        val result = sut(collectibleSearchResponse)

        val expected = CollectibleSearch("primaryImageUrl", "title")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN response fields are null`() {
        val collectibleSearchResponse = CollectibleSearchResponse(null, null)

        val result = sut(collectibleSearchResponse)

        val expected = CollectibleSearch(null, null)
        assertEquals(expected, result)
    }
}

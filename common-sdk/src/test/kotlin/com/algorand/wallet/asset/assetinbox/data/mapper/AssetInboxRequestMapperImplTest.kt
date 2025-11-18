/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.asset.assetinbox.data.mapper

import com.algorand.wallet.asset.assetinbox.data.model.AssetInboxRequestResponse
import com.algorand.wallet.asset.assetinbox.data.model.AssetInboxRequestsResponse
import com.algorand.wallet.asset.assetinbox.domain.model.AssetInboxRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetInboxRequestMapperImplTest {

    private val sut = AssetInboxRequestMapperImpl()

    @Test
    fun `EXPECT asset inbox request list WHEN response has valid data`() {
        val result = sut(RESPONSE)

        val expected = listOf(
            AssetInboxRequest(address = "address", requestCount = 0),
            AssetInboxRequest(address = "address2", requestCount = 10)
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT empty list WHEN response data is null`() {
        val responseData = AssetInboxRequestsResponse(null)

        val result = sut(responseData)

        assertEquals(emptyList<AssetInboxRequest>(), result)
    }

    private companion object {
        val NULL_ADDRESS_RESPONSE = AssetInboxRequestResponse(
            address = null,
            requestCount = 5
        )
        val NULL_REQUEST_COUNT_RESPONSE = AssetInboxRequestResponse(
            address = "address",
            requestCount = null
        )
        val VALID_RESPONSE = AssetInboxRequestResponse(
            address = "address2",
            requestCount = 10
        )

        val RESPONSE = AssetInboxRequestsResponse(
            assetInboxRequests = listOf(
                NULL_ADDRESS_RESPONSE,
                NULL_REQUEST_COUNT_RESPONSE,
                VALID_RESPONSE
            )
        )
    }
}

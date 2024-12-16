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

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.database.model.AssetHoldingEntity
import com.algorand.common.account.info.data.database.model.AssetStatusEntity
import com.algorand.common.account.info.data.model.AssetHoldingResponse
import com.algorand.common.encryption.AddressEncryptionManager
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AssetHoldingEntityMapperImplTest {

    private val addressEncryptionManager: AddressEncryptionManager = mockk {
        every { encrypt(ADDRESS) } returns ENCRYPTED_ADDRESS
    }

    private val sut = AssetHoldingEntityMapperImpl(addressEncryptionManager)

    @Test
    fun `EXPECT null WHEN asset id is null`() {
        val response = RESPONSE.copy(assetId = null)

        val result = sut(ADDRESS, response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN amount is null`() {
        val response = RESPONSE.copy(amount = null)

        val result = sut(ADDRESS, response)

        assertNull(result)
    }

    @Test
    fun `EXPECT mapped entity WHEN response is valid`() {
        val result = sut(ADDRESS, RESPONSE)

        assertEquals(EXPECTED_ENTITY, result)
    }

    @Test
    fun `EXPECT mapped entity with default values WHEN optional fields are missing`() {
        val response = RESPONSE.copy(
            isDeleted = null,
            isFrozen = null
        )

        val result = sut(ADDRESS, response)

        assertEquals(EXPECTED_ENTITY, result)
    }

    private companion object {
        const val ADDRESS = "address"
        const val ENCRYPTED_ADDRESS = "encrypted_address"
        val RESPONSE = AssetHoldingResponse(
            assetId = 1,
            amount = "10",
            isFrozen = false,
            isDeleted = false,
            optedInAtRound = 0,
            optedOutAtRound = null
        )
        val EXPECTED_ENTITY = AssetHoldingEntity(
            encryptedAddress = ENCRYPTED_ADDRESS,
            assetId = 1,
            amount = "10",
            isFrozen = false,
            isDeleted = false,
            optedInAtRound = 0,
            optedOutAtRound = null,
            assetStatusEntity = AssetStatusEntity.OWNED_BY_ACCOUNT
        )
    }
}

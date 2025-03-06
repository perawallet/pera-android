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

package com.algorand.wallet.account.custom.data.mapper.model

import com.algorand.wallet.account.custom.data.database.model.CustomHdSeedInfoEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CustomHdSeedInfoMapperImplTest {

    private lateinit var sut: CustomHdSeedInfoMapperImpl

    @Before
    fun setup() {
        sut = CustomHdSeedInfoMapperImpl()
    }

    @Test
    fun `EXPECT correct domain model WHEN entity has valid data`() {
        val entity = mockk<CustomHdSeedInfoEntity>()
        val seedId = 123
        val entropyCustomName = "Test Seed"
        val orderIndex = 5
        val isBackedUp = true

        every { entity.seedId } returns seedId
        every { entity.entropyCustomName } returns entropyCustomName
        every { entity.orderIndex } returns orderIndex
        every { entity.isBackedUp } returns isBackedUp

        val result = sut.invoke(entity)

        assertEquals(seedId, result.seedId)
        assertEquals(entropyCustomName, result.entropyCustomName)
        assertEquals(orderIndex, result.orderIndex)
        assertEquals(isBackedUp, result.isBackedUp)
    }

    @Test
    fun `EXPECT correct domain model WHEN entity with different values is provided`() {
        val entity = mockk<CustomHdSeedInfoEntity>()
        val seedId = 456
        val entropyCustomName = "Another Seed"
        val orderIndex = 10
        val isBackedUp = false

        every { entity.seedId } returns seedId
        every { entity.entropyCustomName } returns entropyCustomName
        every { entity.orderIndex } returns orderIndex
        every { entity.isBackedUp } returns isBackedUp

        val result = sut.invoke(entity)

        assertEquals(seedId, result.seedId)
        assertEquals(entropyCustomName, result.entropyCustomName)
        assertEquals(orderIndex, result.orderIndex)
        assertEquals(isBackedUp, result.isBackedUp)
    }
}

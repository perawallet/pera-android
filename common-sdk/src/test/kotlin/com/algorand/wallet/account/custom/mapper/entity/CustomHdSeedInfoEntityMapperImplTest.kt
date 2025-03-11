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

package com.algorand.wallet.account.custom.data.mapper.entity

import com.algorand.wallet.account.custom.domain.model.CustomHdSeedInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CustomHdSeedInfoEntityMapperImplTest {

    private lateinit var sut: CustomHdSeedInfoEntityMapperImpl

    @Before
    fun setup() {
        sut = CustomHdSeedInfoEntityMapperImpl()
    }

    @Test
    fun `EXPECT correct entity WHEN domain model has valid data`() {
        val domainModel = mockk<CustomHdSeedInfo>()
        val seedId = 123
        val entropyCustomName = "Test Seed"
        val orderIndex = 5
        val isBackedUp = true

        every { domainModel.seedId } returns seedId
        every { domainModel.entropyCustomName } returns entropyCustomName
        every { domainModel.orderIndex } returns orderIndex
        every { domainModel.isBackedUp } returns isBackedUp

        val result = sut.invoke(domainModel)

        assertEquals(seedId, result.seedId)
        assertEquals(entropyCustomName, result.entropyCustomName)
        assertEquals(orderIndex, result.orderIndex)
        assertEquals(isBackedUp, result.isBackedUp)
    }

    @Test
    fun `EXPECT entity with different values WHEN domain model has different data`() {
        val domainModel = mockk<CustomHdSeedInfo>()
        val seedId = 456
        val entropyCustomName = "Another Seed"
        val orderIndex = 10
        val isBackedUp = false

        every { domainModel.seedId } returns seedId
        every { domainModel.entropyCustomName } returns entropyCustomName
        every { domainModel.orderIndex } returns orderIndex
        every { domainModel.isBackedUp } returns isBackedUp

        val result = sut.invoke(domainModel)

        assertEquals(seedId, result.seedId)
        assertEquals(entropyCustomName, result.entropyCustomName)
        assertEquals(orderIndex, result.orderIndex)
        assertEquals(isBackedUp, result.isBackedUp)
    }
}

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

import com.algorand.wallet.account.custom.data.database.model.CustomAccountInfoEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CustomAccountInfoMapperImplTest {

    private lateinit var sut: CustomAccountInfoMapperImpl

    @Before
    fun setup() {
        sut = CustomAccountInfoMapperImpl()
    }

    @Test
    fun `EXPECT correct domain model WHEN entity has valid data`() {
        val address = "ABCDEF123456"
        val entity = mockk<CustomAccountInfoEntity>()
        val customName = "Test Account"
        val orderIndex = 5
        val isBackedUp = true

        every { entity.customName } returns customName
        every { entity.orderIndex } returns orderIndex
        every { entity.isBackedUp } returns isBackedUp

        val result = sut.invoke(address, entity)

        assertEquals(address, result.address)
        assertEquals(customName, result.customName)
        assertEquals(orderIndex, result.orderIndex)
        assertEquals(isBackedUp, result.isBackedUp)
    }

    @Test
    fun `EXPECT domain model with default values WHEN entity is null`() {
        val address = "ABCDEF123456"
        val entity = null

        val result = sut.invoke(address, entity)

        assertEquals(address, result.address)
        assertEquals(null, result.customName)
        assertEquals(0, result.orderIndex)
        assertEquals(false, result.isBackedUp)
    }

    @Test
    fun `EXPECT domain model with null customName WHEN entity has null customName`() {
        val address = "ABCDEF123456"
        val entity = mockk<CustomAccountInfoEntity>()
        val orderIndex = 3
        val isBackedUp = true

        every { entity.customName } returns null
        every { entity.orderIndex } returns orderIndex
        every { entity.isBackedUp } returns isBackedUp

        val result = sut.invoke(address, entity)

        assertEquals(address, result.address)
        assertEquals(null, result.customName)
        assertEquals(orderIndex, result.orderIndex)
        assertEquals(isBackedUp, result.isBackedUp)
    }

    @Test
    fun `EXPECT domain model with different values WHEN entity has different data`() {
        val address = "XYZABC789012"
        val entity = mockk<CustomAccountInfoEntity>()
        val customName = "Secondary Account"
        val orderIndex = 10
        val isBackedUp = false

        every { entity.customName } returns customName
        every { entity.orderIndex } returns orderIndex
        every { entity.isBackedUp } returns isBackedUp

        val result = sut.invoke(address, entity)

        assertEquals(address, result.address)
        assertEquals(customName, result.customName)
        assertEquals(orderIndex, result.orderIndex)
        assertEquals(isBackedUp, result.isBackedUp)
    }
}
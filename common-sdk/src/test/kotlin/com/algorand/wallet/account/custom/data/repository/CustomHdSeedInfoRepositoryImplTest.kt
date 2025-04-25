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

package com.algorand.wallet.account.custom.data.repository

import com.algorand.wallet.account.custom.data.database.dao.CustomHdSeedInfoDao
import com.algorand.wallet.account.custom.data.database.model.CustomHdSeedInfoEntity
import com.algorand.wallet.account.custom.data.mapper.entity.CustomHdSeedInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomHdSeedInfoMapper
import com.algorand.wallet.account.custom.domain.model.CustomHdSeedInfo
import com.algorand.wallet.account.custom.domain.model.HdSeedOrderIndex
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class CustomHdSeedInfoRepositoryImplTest {

    private lateinit var sut: CustomHdSeedInfoRepositoryImpl
    private lateinit var customHdSeedInfoDao: CustomHdSeedInfoDao
    private lateinit var customHdSeedInfoMapper: CustomHdSeedInfoMapper
    private lateinit var customHdSeedInfoEntityMapper: CustomHdSeedInfoEntityMapper

    @Before
    fun setup() {
        customHdSeedInfoDao = mockk(relaxed = true)
        customHdSeedInfoMapper = mockk()
        customHdSeedInfoEntityMapper = mockk()
        sut = CustomHdSeedInfoRepositoryImpl(
            customHdSeedInfoDao,
            customHdSeedInfoMapper,
            customHdSeedInfoEntityMapper
        )
    }

    @Test
    fun `EXPECT custom info WHEN dao returns valid entity`() = runTest {
        val seedId = 123
        val entity = mockk<CustomHdSeedInfoEntity>()
        val expectedInfo = mockk<CustomHdSeedInfo>()

        coEvery { customHdSeedInfoDao.getOrNull(seedId) } returns entity
        every { customHdSeedInfoMapper.invoke(entity) } returns expectedInfo

        val result = sut.getCustomInfo(seedId)

        assertEquals(expectedInfo, result)
    }

    @Test
    fun `EXPECT null WHEN dao returns null for getCustomInfo`() = runTest {
        val seedId = 123

        coEvery { customHdSeedInfoDao.getOrNull(seedId) } returns null

        val result = sut.getCustomInfo(seedId)

        assertNull(result)
    }

    @Test
    fun `EXPECT custom info WHEN dao returns valid entity for getCustomInfoOrNull`() = runTest {
        val seedId = 123
        val entity = mockk<CustomHdSeedInfoEntity>()
        val expectedInfo = mockk<CustomHdSeedInfo>()

        coEvery { customHdSeedInfoDao.getOrNull(seedId) } returns entity
        every { customHdSeedInfoMapper.invoke(entity) } returns expectedInfo

        val result = sut.getCustomInfoOrNull(seedId)

        assertEquals(expectedInfo, result)
    }

    @Test
    fun `EXPECT null WHEN dao returns null for getCustomInfoOrNull`() = runTest {
        val seedId = 123

        coEvery { customHdSeedInfoDao.getOrNull(seedId) } returns null

        val result = sut.getCustomInfoOrNull(seedId)

        assertNull(result)
    }

    @Test
    fun `EXPECT dao to be called WHEN setCustomInfo is invoked`() = runTest {
        val info = mockk<CustomHdSeedInfo>()
        val entity = mockk<CustomHdSeedInfoEntity>()

        every { customHdSeedInfoEntityMapper.invoke(info) } returns entity
        coEvery { customHdSeedInfoDao.insert(entity) } returns Unit

        val result = sut.setCustomInfo(info)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT dao to be called WHEN setCustomName is invoked`() = runTest {
        val seedId = 123
        val name = "Test Seed"
        var result: Unit? = null

        coEvery { customHdSeedInfoDao.updateCustomName(seedId, name) } returns Unit

        result = sut.setCustomName(seedId, name)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT correct custom name WHEN getCustomName is invoked`() = runTest {
        val seedId = 123
        val expectedName = "Test Seed"

        coEvery { customHdSeedInfoDao.getCustomName(seedId) } returns expectedName

        val result = sut.getCustomName(seedId)

        assertEquals(expectedName, result)
    }

    @Test
    fun `EXPECT dao to be called WHEN deleteCustomInfo is invoked`() = runTest {
        val seedId = 123

        coEvery { customHdSeedInfoDao.delete(seedId) } returns Unit

        val result = sut.deleteCustomInfo(seedId)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT set of not backed up seeds WHEN getNotBackedUpHdSeeds is invoked`() = runTest {
        val notBackedUpSeedIds = listOf(123, 456, 789)

        coEvery { customHdSeedInfoDao.getNotBackedUpSeedIds() } returns notBackedUpSeedIds

        val result = sut.getNotBackedUpHdSeeds()

        assertEquals(notBackedUpSeedIds.toSet(), result)
    }

    @Test
    fun `EXPECT set of backed up seeds WHEN getBackedUpHdSeeds is invoked`() = runTest {
        val backedUpSeedIds = listOf(123, 456, 789)

        coEvery { customHdSeedInfoDao.getBackedUpSeedIds() } returns backedUpSeedIds

        val result = sut.getBackedUpHdSeeds()

        assertEquals(backedUpSeedIds.toSet(), result)
    }

    @Test
    fun `EXPECT true WHEN isHdSeedBackedUp is invoked with backed up seed`() = runTest {
        val seedId = 123

        coEvery { customHdSeedInfoDao.isAccountBackedUp(seedId) } returns true

        val result = sut.isHdSeedBackedUp(seedId)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN isHdSeedBackedUp is invoked with not backed up seed`() = runTest {
        val seedId = 123

        coEvery { customHdSeedInfoDao.isAccountBackedUp(seedId) } returns false

        val result = sut.isHdSeedBackedUp(seedId)

        assertFalse(result)
    }

    @Test
    fun `EXPECT list of order indexes WHEN getAllHdSeedOrderIndexes is invoked`() = runTest {
        val entity1 = mockk<CustomHdSeedInfoEntity>()
        val entity2 = mockk<CustomHdSeedInfoEntity>()
        val entities = listOf(entity1, entity2)

        every { entity1.seedId } returns 123
        every { entity1.orderIndex } returns 1
        every { entity2.seedId } returns 456
        every { entity2.orderIndex } returns 2

        coEvery { customHdSeedInfoDao.getAll() } returns entities

        val result = sut.getAllHdSeedOrderIndexes()

        assertEquals(2, result.size)
        assertEquals(HdSeedOrderIndex(123, 1), result[0])
        assertEquals(HdSeedOrderIndex(456, 2), result[1])
    }

    @Test
    fun `EXPECT dao to be called WHEN setOrderIndex is invoked`() = runTest {
        val seedId = 123
        val orderIndex = 5

        coEvery { customHdSeedInfoDao.updateOrderIndex(seedId, orderIndex) } returns Unit

        val result = sut.setOrderIndex(seedId, orderIndex)

        assertEquals(Unit, result)
    }
}

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

package com.algorand.wallet.account.custom.data.repository

import com.algorand.wallet.account.custom.data.database.dao.CustomAccountInfoDao
import com.algorand.wallet.account.custom.data.database.model.CustomAccountInfoEntity
import com.algorand.wallet.account.custom.data.mapper.entity.CustomAccountInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomAccountInfoMapper
import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
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
class CustomAccountInfoRepositoryImplTest {

    private lateinit var sut: CustomAccountInfoRepositoryImpl
    private lateinit var customAccountInfoDao: CustomAccountInfoDao
    private lateinit var customAccountInfoMapper: CustomAccountInfoMapper
    private lateinit var customAccountInfoEntityMapper: CustomAccountInfoEntityMapper

    @Before
    fun setup() {
        customAccountInfoDao = mockk(relaxed = true)
        customAccountInfoMapper = mockk()
        customAccountInfoEntityMapper = mockk()
        sut = CustomAccountInfoRepositoryImpl(
            customAccountInfoDao,
            customAccountInfoMapper,
            customAccountInfoEntityMapper
        )
    }

    @Test
    fun `EXPECT custom info WHEN dao returns valid entity`() = runTest {
        val address = "ABCDEF123456"
        val entity = mockk<CustomAccountInfoEntity>()
        val expectedInfo = mockk<CustomAccountInfo>()

        coEvery { customAccountInfoDao.getOrNull(address) } returns entity
        every { customAccountInfoMapper.invoke(address, entity) } returns expectedInfo

        val result = sut.getCustomInfo(address)

        assertEquals(expectedInfo, result)
    }

    @Test
    fun `EXPECT mapped info WHEN dao returns null for getCustomInfo`() = runTest {
        val address = "ABCDEF123456"
        val expectedInfo = mockk<CustomAccountInfo>()

        coEvery { customAccountInfoDao.getOrNull(address) } returns null
        every { customAccountInfoMapper.invoke(address, null) } returns expectedInfo

        val result = sut.getCustomInfo(address)

        assertEquals(expectedInfo, result)
    }

    @Test
    fun `EXPECT custom info WHEN dao returns valid entity for getCustomInfoOrNull`() = runTest {
        val address = "ABCDEF123456"
        val entity = mockk<CustomAccountInfoEntity>()
        val expectedInfo = mockk<CustomAccountInfo>()

        coEvery { customAccountInfoDao.getOrNull(address) } returns entity
        every { customAccountInfoMapper.invoke(address, entity) } returns expectedInfo

        val result = sut.getCustomInfoOrNull(address)

        assertEquals(expectedInfo, result)
    }

    @Test
    fun `EXPECT null WHEN dao returns null for getCustomInfoOrNull`() = runTest {
        val address = "ABCDEF123456"

        coEvery { customAccountInfoDao.getOrNull(address) } returns null

        val result = sut.getCustomInfoOrNull(address)

        assertNull(result)
    }

    @Test
    fun `EXPECT dao to be called WHEN setCustomInfo is invoked`() = runTest {
        val info = mockk<CustomAccountInfo>()
        val entity = mockk<CustomAccountInfoEntity>()
        var result: Unit? = null

        every { customAccountInfoEntityMapper.invoke(info) } returns entity
        coEvery { customAccountInfoDao.insert(entity) } returns Unit

        result = sut.setCustomInfo(info)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT dao to be called WHEN setCustomName is invoked`() = runTest {
        val address = "ABCDEF123456"
        val name = "Test Account"

        coEvery { customAccountInfoDao.updateCustomName(address, name) } returns Unit

        val result = sut.setCustomName(address, name)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT correct custom name WHEN getCustomName is invoked`() = runTest {
        val address = "ABCDEF123456"
        val expectedName = "Test Account"

        coEvery { customAccountInfoDao.getCustomName(address) } returns expectedName

        val result = sut.getCustomName(address)

        assertEquals(expectedName, result)
    }

    @Test
    fun `EXPECT dao to be called WHEN deleteCustomInfo is invoked`() = runTest {
        val address = "ABCDEF123456"

        coEvery { customAccountInfoDao.delete(address) } returns Unit

        val result = sut.deleteCustomInfo(address)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT set of not backed up accounts WHEN getNotBackedUpAccounts is invoked`() = runTest {
        val notBackedUpAddresses = listOf("ABCDEF123456", "GHIJKL789012", "MNOPQR345678")

        coEvery { customAccountInfoDao.getNotBackedUpAddresses() } returns notBackedUpAddresses

        val result = sut.getNotBackedUpAccounts()

        assertEquals(notBackedUpAddresses.toSet(), result)
    }

    @Test
    fun `EXPECT set of backed up accounts WHEN getBackedUpAccounts is invoked`() = runTest {
        val backedUpAddresses = listOf("ABCDEF123456", "GHIJKL789012", "MNOPQR345678")

        coEvery { customAccountInfoDao.getBackedUpAddresses() } returns backedUpAddresses

        val result = sut.getBackedUpAccounts()

        assertEquals(backedUpAddresses.toSet(), result)
    }

    @Test
    fun `EXPECT true WHEN isAccountBackedUp is invoked with backed up account`() = runTest {
        val address = "ABCDEF123456"

        coEvery { customAccountInfoDao.isAccountBackedUp(address) } returns true

        val result = sut.isAccountBackedUp(address)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN isAccountBackedUp is invoked with not backed up account`() = runTest {
        val address = "ABCDEF123456"

        coEvery { customAccountInfoDao.isAccountBackedUp(address) } returns false

        val result = sut.isAccountBackedUp(address)

        assertFalse(result)
    }

    @Test
    fun `EXPECT list of order indexes WHEN getAllAccountOrderIndexes is invoked`() = runTest {
        val entity1 = mockk<CustomAccountInfoEntity>()
        val entity2 = mockk<CustomAccountInfoEntity>()
        val entities = listOf(entity1, entity2)

        every { entity1.algoAddress } returns "ABCDEF123456"
        every { entity1.orderIndex } returns 1
        every { entity2.algoAddress } returns "GHIJKL789012"
        every { entity2.orderIndex } returns 2

        coEvery { customAccountInfoDao.getAll() } returns entities

        val result = sut.getAllAccountOrderIndexes()

        assertEquals(2, result.size)
        assertEquals(AccountOrderIndex("ABCDEF123456", 1), result[0])
        assertEquals(AccountOrderIndex("GHIJKL789012", 2), result[1])
    }

    @Test
    fun `EXPECT dao to be called WHEN setOrderIndex is invoked`() = runTest {
        val address = "ABCDEF123456"
        val orderIndex = 5

        coEvery { customAccountInfoDao.updateOrderIndex(address, orderIndex) } returns Unit

        val result = sut.setOrderIndex(address, orderIndex)

        assertEquals(Unit, result)
    }
}

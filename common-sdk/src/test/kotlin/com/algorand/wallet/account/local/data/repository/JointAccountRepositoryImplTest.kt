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

package com.algorand.wallet.account.local.data.repository

import com.algorand.wallet.account.local.data.database.dao.JointDao
import com.algorand.wallet.account.local.data.database.model.JointEntity
import com.algorand.wallet.account.local.data.mapper.entity.JointEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.JointMapper
import com.algorand.wallet.account.local.domain.model.LocalAccount
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class JointAccountRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val jointDao: JointDao = mockk()
    private val jointEntityMapper: JointEntityMapper = mockk()
    private val jointMapper: JointMapper = mockk()

    private val sut = JointAccountRepositoryImpl(
        jointDao,
        jointEntityMapper,
        jointMapper,
        testDispatcher
    )

    @Test
    fun `EXPECT mapped joint accounts WHEN getAll is invoked`() = runTest(testDispatcher) {
        coEvery { jointDao.getAll() } returns listOf(TEST_ENTITY)
        coEvery { jointMapper(TEST_ENTITY) } returns TEST_JOINT_ACCOUNT

        val result = sut.getAll()

        coVerify { jointDao.getAll() }
        coVerify { jointMapper(TEST_ENTITY) }
        assertEquals(1, result.size)
        assertEquals(TEST_JOINT_ACCOUNT, result.first())
    }

    @Test
    fun `EXPECT empty list WHEN getAll is invoked with no accounts`() = runTest(testDispatcher) {
        coEvery { jointDao.getAll() } returns emptyList()

        val result = sut.getAll()

        coVerify { jointDao.getAll() }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT flow of mapped accounts WHEN getAllAsFlow is invoked`() = runTest(testDispatcher) {
        every { jointDao.getAllAsFlow() } returns flowOf(listOf(TEST_ENTITY))
        coEvery { jointMapper(TEST_ENTITY) } returns TEST_JOINT_ACCOUNT

        val result = sut.getAllAsFlow().first()

        assertEquals(1, result.size)
        assertEquals(TEST_JOINT_ACCOUNT, result.first())
    }

    @Test
    fun `EXPECT list of addresses WHEN getAllAddresses is invoked`() = runTest(testDispatcher) {
        val addresses = listOf(TEST_ADDRESS, ANOTHER_ADDRESS)
        coEvery { jointDao.getAllAddresses() } returns addresses

        val result = sut.getAllAddresses()

        coVerify { jointDao.getAllAddresses() }
        assertEquals(addresses, result)
    }

    @Test
    fun `EXPECT mapped account WHEN getAccount is invoked with existing address`() = runTest(testDispatcher) {
        coEvery { jointDao.get(TEST_ADDRESS) } returns TEST_ENTITY
        coEvery { jointMapper(TEST_ENTITY) } returns TEST_JOINT_ACCOUNT

        val result = sut.getAccount(TEST_ADDRESS)

        coVerify { jointDao.get(TEST_ADDRESS) }
        coVerify { jointMapper(TEST_ENTITY) }
        assertEquals(TEST_JOINT_ACCOUNT, result)
    }

    @Test
    fun `EXPECT null WHEN getAccount is invoked with non-existent address`() = runTest(testDispatcher) {
        coEvery { jointDao.get(NON_EXISTENT_ADDRESS) } returns null

        val result = sut.getAccount(NON_EXISTENT_ADDRESS)

        coVerify { jointDao.get(NON_EXISTENT_ADDRESS) }
        assertNull(result)
    }

    @Test
    fun `EXPECT entity inserted WHEN addAccount is invoked`() = runTest(testDispatcher) {
        coEvery { jointEntityMapper(TEST_JOINT_ACCOUNT) } returns TEST_ENTITY
        coEvery { jointDao.insert(TEST_ENTITY) } returns Unit

        sut.addAccount(TEST_JOINT_ACCOUNT)

        coVerify { jointEntityMapper(TEST_JOINT_ACCOUNT) }
        coVerify { jointDao.insert(TEST_ENTITY) }
    }

    @Test
    fun `EXPECT delete called with correct address WHEN deleteAccount is invoked`() = runTest(testDispatcher) {
        coEvery { jointDao.delete(TEST_ADDRESS) } returns Unit

        sut.deleteAccount(TEST_ADDRESS)

        coVerify { jointDao.delete(TEST_ADDRESS) }
    }

    @Test
    fun `EXPECT true WHEN isAddressExists is invoked with existing address`() = runTest(testDispatcher) {
        coEvery { jointDao.isAddressExists(TEST_ADDRESS) } returns true

        val result = sut.isAddressExists(TEST_ADDRESS)

        coVerify { jointDao.isAddressExists(TEST_ADDRESS) }
        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN isAddressExists is invoked with non-existent address`() = runTest(testDispatcher) {
        coEvery { jointDao.isAddressExists(NON_EXISTENT_ADDRESS) } returns false

        val result = sut.isAddressExists(NON_EXISTENT_ADDRESS)

        coVerify { jointDao.isAddressExists(NON_EXISTENT_ADDRESS) }
        assertFalse(result)
    }

    @Test
    fun `EXPECT clearAll called WHEN deleteAllAccounts is invoked`() = runTest(testDispatcher) {
        coEvery { jointDao.clearAll() } returns Unit

        sut.deleteAllAccounts()

        coVerify { jointDao.clearAll() }
    }

    @Test
    fun `EXPECT correct count WHEN getAccountCount is invoked`() = runTest(testDispatcher) {
        val expectedCount = 5
        coEvery { jointDao.getTableSize() } returns expectedCount

        val result = sut.getAccountCount()

        coVerify { jointDao.getTableSize() }
        assertEquals(expectedCount, result)
    }

    @Test
    fun `EXPECT flow with count WHEN getAccountCountAsFlow is invoked`() = runTest(testDispatcher) {
        val expectedCount = 3
        every { jointDao.getTableSizeAsFlow() } returns flowOf(expectedCount)

        val result = sut.getAccountCountAsFlow().first()

        assertEquals(expectedCount, result)
    }

    @Test
    fun `EXPECT multiple accounts mapped WHEN getAll is invoked with multiple accounts`() = runTest(testDispatcher) {
        val entity2 = TEST_ENTITY.copy(algoAddress = ANOTHER_ADDRESS)
        val account2 = TEST_JOINT_ACCOUNT.copy(algoAddress = ANOTHER_ADDRESS)

        coEvery { jointDao.getAll() } returns listOf(TEST_ENTITY, entity2)
        coEvery { jointMapper(TEST_ENTITY) } returns TEST_JOINT_ACCOUNT
        coEvery { jointMapper(entity2) } returns account2

        val result = sut.getAll()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(listOf(TEST_JOINT_ACCOUNT, account2)))
    }

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        const val ANOTHER_ADDRESS = "ANOTHER_ADDRESS_456"
        const val NON_EXISTENT_ADDRESS = "NON_EXISTENT"
        val TEST_PARTICIPANT_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1

        val TEST_ENTITY = JointEntity(
            algoAddress = TEST_ADDRESS,
            participantAddresses = "[\"ADDR1\",\"ADDR2\",\"ADDR3\"]",
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )

        val TEST_JOINT_ACCOUNT = LocalAccount.Joint(
            algoAddress = TEST_ADDRESS,
            participantAddresses = TEST_PARTICIPANT_ADDRESSES,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )
    }
}

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

import com.algorand.wallet.account.local.data.database.dao.HdKeyDao
import com.algorand.wallet.account.local.data.database.model.HdKeyEntity
import com.algorand.wallet.account.local.data.mapper.entity.HdKeyEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.HdKeyMapper
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest

class HdKeyAccountRepositoryImplTest {

    private val hdKeyDao: HdKeyDao = mockk()
    private val hdKeyEntityMapper: HdKeyEntityMapper = mockk()
    private val hdKeyMapper: HdKeyMapper = mockk()
    private val aesPlatformManager: AESPlatformManager = mockk()
    private val sut = HdKeyAccountRepositoryImpl(
        hdKeyDao,
        hdKeyEntityMapper,
        hdKeyMapper,
        aesPlatformManager
    )

    @Test
    fun `EXPECT all accounts as flow WHEN getAllAsFlow is invoked`() = runTest {
        val entities = listOf(
            HdKeyEntity("address1", byteArrayOf(1), byteArrayOf(2), 1, 0, 0, 0, 1),
            HdKeyEntity("address2", byteArrayOf(3), byteArrayOf(4), 2, 0, 0, 1, 1)
        )
        val expectedAccounts = listOf(
            LocalAccount.HdKey("address1", byteArrayOf(1), 1, 0, 0, 0, 1),
            LocalAccount.HdKey("address2", byteArrayOf(3), 2, 0, 0, 1, 1)
        )

        coEvery { hdKeyDao.getAllAsFlow() } returns flowOf(entities)
        coEvery { hdKeyMapper(entities[0]) } returns expectedAccounts[0]
        coEvery { hdKeyMapper(entities[1]) } returns expectedAccounts[1]

        val result = sut.getAllAsFlow().toList().first()

        coVerify { hdKeyDao.getAllAsFlow() }
        assertEquals(expectedAccounts, result)
    }

    @Test
    fun `EXPECT account count as flow WHEN getAccountCountAsFlow is invoked`() = runTest {
        val expectedCount = 3
        coEvery { hdKeyDao.getTableSizeAsFlow() } returns flowOf(expectedCount)

        val flow = sut.getAccountCountAsFlow()
        val result = flow.toList()

        assertEquals(listOf(expectedCount), result)
    }

    @Test
    fun `EXPECT account count WHEN getAccountCount is invoked`() = runTest {
        val expectedCount = 3
        coEvery { hdKeyDao.getTableSize() } returns expectedCount

        val result = sut.getAccountCount()

        assertEquals(expectedCount, result)
    }

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(
            HdKeyEntity("address1", byteArrayOf(1), byteArrayOf(2), 1, 0, 0, 0, 1),
            HdKeyEntity("address2", byteArrayOf(3), byteArrayOf(4), 2, 0, 0, 1, 1)
        )
        val expectedAccounts = listOf(
            LocalAccount.HdKey("address1", byteArrayOf(1), 1, 0, 0, 0, 1),
            LocalAccount.HdKey("address2", byteArrayOf(3), 2, 0, 0, 1, 1)
        )

        coEvery { hdKeyDao.getAll() } returns entities
        coEvery { hdKeyMapper(entities[0]) } returns expectedAccounts[0]
        coEvery { hdKeyMapper(entities[1]) } returns expectedAccounts[1]

        val result = sut.getAll()

        coVerify { hdKeyDao.getAll() }
        assertEquals(expectedAccounts, result)
    }

    @Test
    fun `EXPECT all addresses WHEN getAllAddresses is invoked`() = runTest {
        val addresses = listOf("address1", "address2")
        coEvery { hdKeyDao.getAllAddresses() } returns addresses

        val result = sut.getAllAddresses()
        assertEquals(addresses, result)
    }

    @Test
    fun `EXPECT account WHEN getAccount is invoked`() = runTest {
        val entity = HdKeyEntity("address1", byteArrayOf(1), byteArrayOf(2), 1, 0, 0, 0, 1)
        val expectedAccount = LocalAccount.HdKey("address1", byteArrayOf(1), 1, 0, 0, 0, 1)

        coEvery { hdKeyDao.get("address1") } returns entity
        coEvery { hdKeyMapper(entity) } returns expectedAccount

        val result = sut.getAccount("address1")

        coVerify { hdKeyDao.get("address1") }
        assertEquals(expectedAccount, result)
    }

    @Test
    fun `EXPECT null WHEN getAccount is invoked with a non-existent address`() = runTest {
        coEvery { hdKeyDao.get("non_existent_address") } returns null

        val result = sut.getAccount("non_existent_address")

        coVerify { hdKeyDao.get("non_existent_address") }
        assertNull(result)
    }

    @Test
    fun `EXPECT account to be added WHEN addAccount is invoked`() = runTest {
        val privateKey = byteArrayOf(5, 6, 7)
        val account = LocalAccount.HdKey("address", byteArrayOf(8), 1, 0, 0, 0, 1)
        val entity = HdKeyEntity("address", byteArrayOf(8), privateKey, 1, 0, 0, 0, 1)

        coEvery { hdKeyEntityMapper(account, privateKey) } returns entity
        coEvery { hdKeyDao.insert(entity) } returns Unit

        val result = sut.addAccount(account, privateKey)

        coVerify { hdKeyDao.insert(entity) }
        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT account to be deleted WHEN deleteAccount is invoked`() = runTest {
        coEvery { hdKeyDao.delete("address") } returns Unit

        val result = sut.deleteAccount("address")

        coVerify { hdKeyDao.delete("address") }
        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT all accounts to be deleted WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { hdKeyDao.clearAll() } returns Unit

        val result = sut.deleteAllAccounts()

        coVerify { hdKeyDao.clearAll() }
        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT secret key WHEN getPrivateKey is invoked`() = runTest {
        val encryptedSK = "encryptedSecretKey".toByteArray()
        val decryptedSK = byteArrayOf(1, 2, 3)
        coEvery { hdKeyDao.get("address") } returns HdKeyEntity("address", byteArrayOf(8), encryptedSK, 1, 0, 0, 0, 1)
        coEvery { aesPlatformManager.decryptByteArray(encryptedSK) } returns decryptedSK

        val result = sut.getPrivateKey("address")
        assertEquals(decryptedSK, result)
    }

    @Test
    fun `EXPECT null WHEN getPrivateKey is invoked with a non-existent address`() = runTest {
        coEvery { hdKeyDao.get("non_existent_address") } returns null

        val result = sut.getPrivateKey("non_existent_address")

        coVerify { hdKeyDao.get("non_existent_address") }
        assertEquals(null, result)
    }
}

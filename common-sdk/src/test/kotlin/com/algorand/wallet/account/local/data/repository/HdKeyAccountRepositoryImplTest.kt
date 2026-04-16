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
import com.algorand.wallet.account.local.data.mapper.model.HdWalletSummaryMapper
import com.algorand.wallet.account.local.domain.model.HdWalletSummary
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class HdKeyAccountRepositoryImplTest {

    private val hdKeyDao: HdKeyDao = mockk()
    private val hdKeyEntityMapper: HdKeyEntityMapper = mockk()
    private val hdKeyMapper: HdKeyMapper = mockk()
    private val hdWalletSummaryMapper: HdWalletSummaryMapper = mockk()
    private val aesPlatformManager: AESPlatformManager = mockk()

    private val sut = HdKeyAccountRepositoryImpl(
        hdKeyDao,
        hdKeyEntityMapper,
        hdWalletSummaryMapper,
        hdKeyMapper,
        aesPlatformManager
    )

    @Test
    fun `EXPECT all accounts as flow WHEN getAllAsFlow is invoked`() = runTest {
        val entities = listOf(TEST_ENTITY_1, TEST_ENTITY_2)
        val expectedAccounts = listOf(TEST_ACCOUNT_1, TEST_ACCOUNT_2)

        every { hdKeyDao.getAllAsFlow() } returns flowOf(entities)
        coEvery { hdKeyMapper(TEST_ENTITY_1) } returns TEST_ACCOUNT_1
        coEvery { hdKeyMapper(TEST_ENTITY_2) } returns TEST_ACCOUNT_2

        val result = sut.getAllAsFlow().first()

        assertEquals(expectedAccounts, result)
    }

    @Test
    fun `EXPECT empty list WHEN getAllAsFlow is invoked with no accounts`() = runTest {
        every { hdKeyDao.getAllAsFlow() } returns flowOf(emptyList())

        val result = sut.getAllAsFlow().first()

        assertEquals(emptyList<LocalAccount.HdKey>(), result)
    }

    @Test
    fun `EXPECT account count as flow WHEN getAccountCountAsFlow is invoked`() = runTest {
        val expectedCount = 3
        every { hdKeyDao.getTableSizeAsFlow() } returns flowOf(expectedCount)

        val result = sut.getAccountCountAsFlow().toList()

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
        val entities = listOf(TEST_ENTITY_1, TEST_ENTITY_2)
        val expectedAccounts = listOf(TEST_ACCOUNT_1, TEST_ACCOUNT_2)

        coEvery { hdKeyDao.getAll() } returns entities
        coEvery { hdKeyMapper(TEST_ENTITY_1) } returns TEST_ACCOUNT_1
        coEvery { hdKeyMapper(TEST_ENTITY_2) } returns TEST_ACCOUNT_2

        val result = sut.getAll()

        coVerify { hdKeyDao.getAll() }
        assertEquals(expectedAccounts, result)
    }

    @Test
    fun `EXPECT all addresses WHEN getAllAddresses is invoked`() = runTest {
        val addresses = listOf(ADDRESS_1, ADDRESS_2)
        coEvery { hdKeyDao.getAllAddresses() } returns addresses

        val result = sut.getAllAddresses()

        assertEquals(addresses, result)
    }

    @Test
    fun `EXPECT account WHEN getAccount is invoked with existing address`() = runTest {
        coEvery { hdKeyDao.get(ADDRESS_1) } returns TEST_ENTITY_1
        coEvery { hdKeyMapper(TEST_ENTITY_1) } returns TEST_ACCOUNT_1

        val result = sut.getAccount(ADDRESS_1)

        coVerify { hdKeyDao.get(ADDRESS_1) }
        assertEquals(TEST_ACCOUNT_1, result)
    }

    @Test
    fun `EXPECT null WHEN getAccount is invoked with non-existent address`() = runTest {
        coEvery { hdKeyDao.get(NON_EXISTENT_ADDRESS) } returns null

        val result = sut.getAccount(NON_EXISTENT_ADDRESS)

        coVerify { hdKeyDao.get(NON_EXISTENT_ADDRESS) }
        assertNull(result)
    }

    @Test
    fun `EXPECT account to be added WHEN addAccount is invoked`() = runTest {
        val privateKey = byteArrayOf(5, 6, 7)

        coEvery { hdKeyEntityMapper(TEST_ACCOUNT_1, privateKey) } returns TEST_ENTITY_1
        coEvery { hdKeyDao.insert(TEST_ENTITY_1) } returns Unit

        sut.addAccount(TEST_ACCOUNT_1, privateKey)

        coVerify { hdKeyDao.insert(TEST_ENTITY_1) }
    }

    @Test
    fun `EXPECT account to be deleted WHEN deleteAccount is invoked`() = runTest {
        coEvery { hdKeyDao.delete(ADDRESS_1) } returns Unit

        sut.deleteAccount(ADDRESS_1)

        coVerify { hdKeyDao.delete(ADDRESS_1) }
    }

    @Test
    fun `EXPECT all accounts to be deleted WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { hdKeyDao.clearAll() } returns Unit

        sut.deleteAllAccounts()

        coVerify { hdKeyDao.clearAll() }
    }

    @Test
    fun `EXPECT decrypted secret key WHEN getPrivateKey is invoked with existing address`() = runTest {
        val encryptedSK = "encryptedSecretKey".toByteArray()
        val decryptedSK = byteArrayOf(1, 2, 3)
        val entityWithEncryptedKey = TEST_ENTITY_1.copy(encryptedPrivateKey = encryptedSK)

        coEvery { hdKeyDao.get(ADDRESS_1) } returns entityWithEncryptedKey
        coEvery { aesPlatformManager.decryptByteArray(encryptedSK) } returns decryptedSK

        val result = sut.getPrivateKey(ADDRESS_1)

        assertEquals(decryptedSK, result)
    }

    @Test
    fun `EXPECT null WHEN getPrivateKey is invoked with non-existent address`() = runTest {
        coEvery { hdKeyDao.get(NON_EXISTENT_ADDRESS) } returns null

        val result = sut.getPrivateKey(NON_EXISTENT_ADDRESS)

        coVerify { hdKeyDao.get(NON_EXISTENT_ADDRESS) }
        assertNull(result)
    }

    @Test
    fun `EXPECT wallet summaries grouped by seedId WHEN getHdWalletSummaries is invoked`() = runTest {
        val entities = listOf(
            createEntity(ADDRESS_1, SEED_ID_1, accountIndex = 0),
            createEntity(ADDRESS_2, SEED_ID_1, accountIndex = 1),
            createEntity("addr3", SEED_ID_1, accountIndex = 2),
            createEntity("addr4", SEED_ID_2, accountIndex = 0)
        )

        val expectedSummary1 = HdWalletSummary(
            seedId = SEED_ID_1,
            accountCount = 3,
            maxAccountIndex = 2,
            addresses = listOf(ADDRESS_1, ADDRESS_2, "addr3")
        )
        val expectedSummary2 = HdWalletSummary(
            seedId = SEED_ID_2,
            accountCount = 1,
            maxAccountIndex = 0,
            addresses = listOf("addr4")
        )

        coEvery { hdKeyDao.getAll() } returns entities
        coEvery { hdWalletSummaryMapper(entities[2], listOf(ADDRESS_1, ADDRESS_2, "addr3")) } returns expectedSummary1
        coEvery { hdWalletSummaryMapper(entities[3], listOf("addr4")) } returns expectedSummary2

        val result = sut.getHdWalletSummaries()

        coVerify { hdKeyDao.getAll() }
        assertEquals(listOf(expectedSummary1, expectedSummary2), result)
    }

    private fun createEntity(
        address: String,
        seedId: Int,
        accountIndex: Int
    ) = HdKeyEntity(
        algoAddress = address,
        publicKey = byteArrayOf(1),
        encryptedPrivateKey = byteArrayOf(2),
        seedId = seedId,
        account = accountIndex,
        change = 0,
        keyIndex = 0,
        derivationType = 1
    )

    private companion object {
        const val ADDRESS_1 = "address1"
        const val ADDRESS_2 = "address2"
        const val NON_EXISTENT_ADDRESS = "non_existent_address"
        const val SEED_ID_1 = 100
        const val SEED_ID_2 = 200

        val TEST_ENTITY_1 = HdKeyEntity(
            algoAddress = ADDRESS_1,
            publicKey = byteArrayOf(1),
            encryptedPrivateKey = byteArrayOf(2),
            seedId = 1,
            account = 0,
            change = 0,
            keyIndex = 0,
            derivationType = 1
        )
        val TEST_ENTITY_2 = HdKeyEntity(
            algoAddress = ADDRESS_2,
            publicKey = byteArrayOf(3),
            encryptedPrivateKey = byteArrayOf(4),
            seedId = 2,
            account = 0,
            change = 0,
            keyIndex = 1,
            derivationType = 1
        )
        val TEST_ACCOUNT_1 = LocalAccount.HdKey(
            algoAddress = ADDRESS_1,
            publicKey = byteArrayOf(1),
            seedId = 1,
            account = 0,
            change = 0,
            keyIndex = 0,
            derivationType = 1
        )
        val TEST_ACCOUNT_2 = LocalAccount.HdKey(
            algoAddress = ADDRESS_2,
            publicKey = byteArrayOf(3),
            seedId = 2,
            account = 0,
            change = 0,
            keyIndex = 1,
            derivationType = 1
        )
    }
}

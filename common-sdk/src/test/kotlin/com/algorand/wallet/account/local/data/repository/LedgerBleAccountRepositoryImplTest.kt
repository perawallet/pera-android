/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.account.local.data.database.dao.LedgerBleDao
import com.algorand.wallet.account.local.data.database.model.LedgerBleEntity
import com.algorand.wallet.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.LedgerBleMapper
import com.algorand.wallet.account.local.domain.model.LocalAccount
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LedgerBleAccountRepositoryImplTest {

    private val ledgerBleDao: LedgerBleDao = mockk()
    private val ledgerBleEntityMapper: LedgerBleEntityMapper = mockk()
    private val ledgerBleMapper: LedgerBleMapper = mockk()
    private val sut = LedgerBleAccountRepositoryImpl(
        ledgerBleDao,
        ledgerBleEntityMapper,
        ledgerBleMapper
    )

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(LEDGER_BLE_1_ENTITY, LEDGER_BLE_2_ENTITY)
        coEvery { ledgerBleDao.getAll() } returns entities
        coEvery { ledgerBleMapper(LEDGER_BLE_1_ENTITY) } returns LEDGER_BLE_1
        coEvery { ledgerBleMapper(LEDGER_BLE_2_ENTITY) } returns LEDGER_BLE_2

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(LEDGER_BLE_1, LEDGER_BLE_2)
        coVerify { ledgerBleDao.getAll() }
        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN getAccount is invoked`() = runTest {
        coEvery { ledgerBleDao.get(LEDGER_BLE_1_ENTITY.algoAddress) } returns LEDGER_BLE_1_ENTITY
        coEvery { ledgerBleMapper(LEDGER_BLE_1_ENTITY) } returns LEDGER_BLE_1

        val localAccount = sut.getAccount(LEDGER_BLE_1_ENTITY.algoAddress)

        coVerify { ledgerBleDao.get(LEDGER_BLE_1_ENTITY.algoAddress) }
        assertEquals(LEDGER_BLE_1, localAccount)
    }

    @Test
    fun `EXPECT null WHEN getAccount is invoked with a non-existent address`() = runTest {
        coEvery { ledgerBleDao.get("non_existent_address") } returns null

        val result = sut.getAccount("non_existent_address")

        coVerify { ledgerBleDao.get("non_existent_address") }
        assertEquals(null, result)
    }

    @Test
    fun `EXPECT account count as flow WHEN getAccountCountAsFlow is invoked`() = runTest {
        val expectedCountFlow = MutableStateFlow(3)
        coEvery { ledgerBleDao.getTableSizeAsFlow() } returns expectedCountFlow

        val testObserver = sut.getAccountCountAsFlow().test()
        expectedCountFlow.update { 5 }

        testObserver.assertValueHistory(3, 5)
    }

    @Test
    fun `EXPECT account count WHEN getAccountCount is invoked`() = runTest {
        val expectedCount = 3
        coEvery { ledgerBleDao.getTableSize() } returns expectedCount

        val result = sut.getAccountCount()

        assertEquals(expectedCount, result)
    }

    @Test
    fun `EXPECT all addresses WHEN getAllAddresses is invoked`() = runTest {
        val addresses = listOf("address1", "address2")
        coEvery { ledgerBleDao.getAllAddresses() } returns addresses

        val result = sut.getAllAddresses()

        assertEquals(addresses, result)
    }

    @Test
    fun `EXPECT account to be added to database WHEN addAccount is invoked`() = runTest {
        coEvery { ledgerBleEntityMapper(LEDGER_BLE_1) } returns LEDGER_BLE_1_ENTITY
        coEvery { ledgerBleDao.insert(LEDGER_BLE_1_ENTITY) } returns Unit

        sut.addAccount(LEDGER_BLE_1)

        coVerify { ledgerBleDao.insert(LEDGER_BLE_1_ENTITY) }
    }

    @Test
    fun `EXPECT account to be deleted from database WHEN deleteAccount is invoked`() = runTest {
        coEvery { ledgerBleDao.delete("address") } returns Unit

        sut.deleteAccount("address")

        coVerify { ledgerBleDao.delete("address") }
    }

    @Test
    fun `EXPECT all accounts to be deleted from database WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { ledgerBleDao.clearAll() } returns Unit

        sut.deleteAllAccounts()

        coVerify { ledgerBleDao.clearAll() }
    }

    @Test
    fun `EXPECT all accounts as flow WHEN getAllAsFlow is invoked`() = runTest {
        val entitiesFlow = MutableStateFlow(
            listOf(
                LEDGER_BLE_1_ENTITY,
                LEDGER_BLE_2_ENTITY
            )
        )
        val expectedAccounts = listOf(
            LEDGER_BLE_1,
            LEDGER_BLE_2
        )

        coEvery { ledgerBleDao.getAllAsFlow() } returns entitiesFlow
        coEvery { ledgerBleMapper(LEDGER_BLE_1_ENTITY) } returns expectedAccounts[0]
        coEvery { ledgerBleMapper(LEDGER_BLE_2_ENTITY) } returns expectedAccounts[1]

        val testObserver = sut.getAllAsFlow().test()
        entitiesFlow.update { emptyList() }

        testObserver.assertValueHistory(expectedAccounts, emptyList())
    }

    companion object {
        private val LEDGER_BLE_1 = peraFixture<LocalAccount.LedgerBle>()
        private val LEDGER_BLE_1_ENTITY = peraFixture<LedgerBleEntity>()
        private val LEDGER_BLE_2 = peraFixture<LocalAccount.LedgerBle>()
        private val LEDGER_BLE_2_ENTITY = peraFixture<LedgerBleEntity>()
    }
}

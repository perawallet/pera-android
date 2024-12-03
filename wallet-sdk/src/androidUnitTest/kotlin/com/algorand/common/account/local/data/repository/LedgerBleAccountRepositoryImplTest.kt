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

package com.algorand.common.account.local.data.repository

import com.algorand.common.account.local.data.database.dao.LedgerBleDao
import com.algorand.common.account.local.data.database.model.LedgerBleEntity
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.common.account.local.data.mapper.model.ledgerble.LedgerBleMapper
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.encryption.AddressEncryptionManager
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LedgerBleAccountRepositoryImplTest {

    private val ledgerBleDao: LedgerBleDao = mockk()
    private val ledgerBleEntityMapper: LedgerBleEntityMapper = mockk()
    private val ledgerBleMapper: LedgerBleMapper = mockk()
    private val encryptionManager: AddressEncryptionManager = mockk()
    private val sut = LedgerBleAccountRepositoryImpl(
        ledgerBleDao,
        ledgerBleEntityMapper,
        ledgerBleMapper,
        encryptionManager
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
    fun `EXPECT account WHEN account was registered before`() = runTest {
        coEvery { ledgerBleMapper(LEDGER_BLE_1_ENTITY) } returns LEDGER_BLE_1
        coEvery { ledgerBleDao.get(LEDGER_BLE_1_ENTITY.encryptedAddress) } returns LEDGER_BLE_1_ENTITY
        coEvery { encryptionManager.encrypt(LEDGER_BLE_1.address) } returns LEDGER_BLE_1_ENTITY.encryptedAddress

        val localAccount = sut.getAccount(LEDGER_BLE_1.address)

        coVerify { ledgerBleMapper(LEDGER_BLE_1_ENTITY) }
        assertEquals(LEDGER_BLE_1, localAccount)
    }

    @Test
    fun `EXPECT account to be added to database  WHEN addAccount is invoked`() = runTest {
        coEvery { ledgerBleDao.insert(LEDGER_BLE_1_ENTITY) } returns Unit
        coEvery { ledgerBleEntityMapper(LEDGER_BLE_1) } returns LEDGER_BLE_1_ENTITY

        sut.addAccount(LEDGER_BLE_1)

        coVerify { ledgerBleDao.insert(LEDGER_BLE_1_ENTITY) }
    }

    @Test
    fun `EXPECT account to be deleted WHEN deleteAccount is invoked`() = runTest {
        coEvery { encryptionManager.encrypt("address") } returns "encryptedAddress"
        coEvery { ledgerBleDao.delete("encryptedAddress") } returns Unit

        sut.deleteAccount("address")

        coVerify { ledgerBleDao.delete("encryptedAddress") }
    }

    @Test
    fun `EXPECT all accounts to be deleted WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { ledgerBleDao.clearAll() } returns Unit

        sut.deleteAllAccounts()

        coVerify { ledgerBleDao.clearAll() }
    }

    @Test
    fun `EXPECT all accounts WHEN getAllAsFlow is invoked`() = runTest {
        val entities = listOf(LEDGER_BLE_1_ENTITY, LEDGER_BLE_2_ENTITY)
        coEvery { ledgerBleDao.getAllAsFlow() } returns flowOf(entities)
        coEvery { ledgerBleMapper(LEDGER_BLE_1_ENTITY) } returns LEDGER_BLE_1
        coEvery { ledgerBleMapper(LEDGER_BLE_2_ENTITY) } returns LEDGER_BLE_2

        val localAccounts = sut.getAllAsFlow().toList().first()

        val expectedReturnedList = listOf(LEDGER_BLE_1, LEDGER_BLE_2)
        coVerify { ledgerBleDao.getAllAsFlow() }
        assertEquals(expectedReturnedList, localAccounts)
    }

    companion object {
        private val LEDGER_BLE_1 = peraFixture<LocalAccount.LedgerBle>()
        private val LEDGER_BLE_1_ENTITY = peraFixture<LedgerBleEntity>()
        private val LEDGER_BLE_2 = peraFixture<LocalAccount.LedgerBle>()
        private val LEDGER_BLE_2_ENTITY = peraFixture<LedgerBleEntity>()
    }
}

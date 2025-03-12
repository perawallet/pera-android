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

package com.algorand.wallet.account.local.data.repository

import com.algorand.test.test
import com.algorand.wallet.account.local.data.database.dao.NoAuthDao
import com.algorand.wallet.account.local.data.database.model.NoAuthEntity
import com.algorand.wallet.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.NoAuthMapper
import com.algorand.wallet.account.local.domain.model.LocalAccount
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NoAuthAccountRepositoryImplTest {

    private val noAuthDao: NoAuthDao = mockk()
    private val noAuthEntityMapper: NoAuthEntityMapper = mockk()
    private val noAuthMapper: NoAuthMapper = mockk()
    private val sut = NoAuthAccountRepositoryImpl(
        noAuthDao,
        noAuthEntityMapper,
        noAuthMapper
    )

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(NoAuthEntity("address1"), NoAuthEntity("address2"))
        coEvery { noAuthDao.getAll() } returns entities
        coEvery { noAuthMapper(entities[0]) } returns LocalAccount.NoAuth("address1")
        coEvery { noAuthMapper(entities[1]) } returns LocalAccount.NoAuth("address2")

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(LocalAccount.NoAuth("address1"), LocalAccount.NoAuth("address2"))
        coVerify { noAuthDao.getAll() }
        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN getAccount is invoked`() = runTest {
        coEvery { noAuthDao.get("address1") } returns NoAuthEntity("address1")
        coEvery { noAuthMapper(NoAuthEntity("address1")) } returns LocalAccount.NoAuth("address1")

        val localAccount = sut.getAccount("address1")

        coVerify { noAuthDao.get("address1") }
        assertEquals(LocalAccount.NoAuth("address1"), localAccount)
    }

    @Test
    fun `EXPECT null WHEN getAccount is invoked with a non-existent address`() = runTest {
        coEvery { noAuthDao.get("non_existent_address") } returns null

        val result = sut.getAccount("non_existent_address")

        coVerify { noAuthDao.get("non_existent_address") }
        assertEquals(null, result)
    }

    @Test
    fun `EXPECT account count as flow WHEN getAccountCountAsFlow is invoked`() = runTest {
        val expectedCountFlow = MutableStateFlow(3)
        coEvery { noAuthDao.getTableSizeAsFlow() } returns expectedCountFlow

        val testObserver = sut.getAccountCountAsFlow().test()
        expectedCountFlow.update { 5 }

        testObserver.assertValueHistory(3, 5)
    }

    @Test
    fun `EXPECT account count WHEN getAccountCount is invoked`() = runTest {
        val expectedCount = 3
        coEvery { noAuthDao.getTableSize() } returns expectedCount

        val result = sut.getAccountCount()

        assertEquals(expectedCount, result)
    }

    @Test
    fun `EXPECT all addresses WHEN getAllAddresses is invoked`() = runTest {
        val addresses = listOf("address1", "address2")
        coEvery { noAuthDao.getAllAddresses() } returns addresses

        val result = sut.getAllAddresses()

        assertEquals(addresses, result)
    }

    @Test
    fun `EXPECT account to be added to database WHEN addAccount is invoked`() = runTest {
        val account = LocalAccount.NoAuth("address")
        val entity = NoAuthEntity("address")
        coEvery { noAuthEntityMapper(account) } returns entity
        coEvery { noAuthDao.insert(entity) } returns Unit

        sut.addAccount(account)

        coVerify { noAuthDao.insert(entity) }
    }

    @Test
    fun `EXPECT account to be deleted from database WHEN deleteAccount is invoked`() = runTest {
        coEvery { noAuthDao.delete("address") } returns Unit

        sut.deleteAccount("address")

        coVerify { noAuthDao.delete("address") }
    }

    @Test
    fun `EXPECT all accounts to be deleted from database WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { noAuthDao.clearAll() } returns Unit

        sut.deleteAllAccounts()

        coVerify { noAuthDao.clearAll() }
    }

    @Test
    fun `EXPECT all accounts as flow WHEN getAllAsFlow is invoked`() = runTest {
        val entitiesFlow = MutableStateFlow(
            listOf(
                NoAuthEntity("address1"),
                NoAuthEntity("address2")
            )
        )
        val expectedAccounts = listOf(
            LocalAccount.NoAuth("address1"),
            LocalAccount.NoAuth("address2")
        )

        coEvery { noAuthDao.getAllAsFlow() } returns entitiesFlow
        coEvery { noAuthMapper(entitiesFlow.value[0]) } returns expectedAccounts[0]
        coEvery { noAuthMapper(entitiesFlow.value[1]) } returns expectedAccounts[1]

        val testObserver = sut.getAllAsFlow().test()
        entitiesFlow.update { emptyList() }

        testObserver.assertValueHistory(expectedAccounts, emptyList())
    }

    @Test
    fun `EXPECT true WHEN isAddressExists returns true`() = runTest {
        coEvery { noAuthDao.isAddressExists("address1") } returns true

        val result = sut.isAddressExists("address1")

        assertEquals(true, result)
    }

    @Test
    fun `EXPECT false WHEN isAddressExists returns false`() = runTest {
        coEvery { noAuthDao.isAddressExists("address1") } returns false

        val result = sut.isAddressExists("address1")

        assertEquals(false, result)
    }
}

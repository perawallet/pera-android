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

import com.algorand.test.test
import com.algorand.wallet.account.local.data.database.dao.Algo25Dao
import com.algorand.wallet.account.local.data.database.model.Algo25Entity
import com.algorand.wallet.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.wallet.account.local.data.mapper.model.Algo25Mapper
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest

class Algo25AccountRepositoryImplTest {

    private val algo25Dao: Algo25Dao = mockk()
    private val algo25EntityMapper: Algo25EntityMapper = mockk()
    private val algo25Mapper: Algo25Mapper = mockk()
    private val aesPlatformManager: AESPlatformManager = mockk()
    private val sut = Algo25AccountRepositoryImpl(
        algo25Dao,
        algo25EntityMapper,
        algo25Mapper,
        aesPlatformManager
    )

    @Test
    fun `EXPECT all accounts as flow WHEN getAllAsFlow is invoked`() = runTest {
        val entitiesFlow = MutableStateFlow(
            listOf(
                Algo25Entity("address1", byteArrayOf()),
                Algo25Entity("address2", byteArrayOf())
            )
        )
        val expectedAccounts = listOf(
            LocalAccount.Algo25("address1"),
            LocalAccount.Algo25("address2")
        )

        coEvery { algo25Dao.getAllAsFlow() } returns entitiesFlow
        coEvery { algo25Mapper(entitiesFlow.value[0]) } returns expectedAccounts[0]
        coEvery { algo25Mapper(entitiesFlow.value[1]) } returns expectedAccounts[1]

        val testObserver = sut.getAllAsFlow().test()
        entitiesFlow.update { emptyList() }

        testObserver.assertValueHistory(expectedAccounts, emptyList())
    }

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(
            Algo25Entity("encryptedAddress1", "encryptedSecretKey1".toByteArray()),
            Algo25Entity("encryptedAddress2", "encryptedSecretKey2".toByteArray())
        )
        coEvery { algo25Dao.getAll() } returns entities
        coEvery { algo25Mapper(entities[0]) } returns LocalAccount.Algo25("address1")
        coEvery { algo25Mapper(entities[1]) } returns LocalAccount.Algo25("address2")

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(
            LocalAccount.Algo25("address1"),
            LocalAccount.Algo25("address2")
        )

        coVerify { algo25Dao.getAll() }

        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN getAccount is invoked`() = runTest {
        val entity = Algo25Entity("address", "encryptedSecretKey".toByteArray())
        coEvery { algo25Dao.get("address") } returns entity
        coEvery { algo25Mapper(entity) } returns LocalAccount.Algo25("address")

        val localAccount = sut.getAccount("address")

        val expectedAccount = LocalAccount.Algo25("address")
        coVerify { algo25Dao.get("address") }
        assertEquals(expectedAccount, localAccount)
    }

    @Test
    fun `EXPECT null WHEN getAccount is invoked with a non-existent address`() = runTest {
        coEvery { algo25Dao.get("non_existent_address") } returns null

        val result = sut.getAccount("non_existent_address")

        coVerify { algo25Dao.get("non_existent_address") }
        assertEquals(null, result)
    }

    @Test
    fun `EXPECT account count as flow WHEN getAccountCountAsFlow is invoked`() = runTest {
        val expectedCountFlow = MutableStateFlow(5)
        coEvery { algo25Dao.getTableSizeAsFlow() } returns expectedCountFlow

        val testObserver = sut.getAccountCountAsFlow().test()
        expectedCountFlow.update { 10 }

        testObserver.assertValueHistory(5, 10)
    }

    @Test
    fun `EXPECT account count WHEN getAccountCount is invoked`() = runTest {
        val expectedCount = 3
        coEvery { algo25Dao.getTableSize() } returns expectedCount

        val result = sut.getAccountCount()

        assertEquals(expectedCount, result)
    }

    @Test
    fun `EXPECT all addresses WHEN getAllAddresses is invoked`() = runTest {
        val addresses = listOf("address1", "address2", "address3")
        coEvery { algo25Dao.getAllAddresses() } returns addresses

        val result = sut.getAllAddresses()

        assertEquals(addresses, result)
    }

    @Test
    fun `EXPECT account to be added to database WHEN addAccount is invoked`() = runTest {
        val privateKey = byteArrayOf(1, 2, 3)
        val account = LocalAccount.Algo25("address")
        val algo25Entity = Algo25Entity("address", byteArrayOf())
        coEvery { algo25EntityMapper.invoke(account, privateKey) } returns algo25Entity
        coEvery { algo25Dao.insert(algo25Entity) } returns Unit

        val result = sut.addAccount(account, privateKey)

        coVerify { algo25Dao.insert(algo25Entity) }
        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT account to be deleted from database WHEN deleteAccount is invoked`() = runTest {
        val address = "address"
        coEvery { algo25Dao.delete(address) } returns Unit

        val result = sut.deleteAccount(address)

        coVerify { algo25Dao.delete(address) }
        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT all accounts to be deleted from database WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { algo25Dao.clearAll() } returns Unit

        val result = sut.deleteAllAccounts()

        coVerify { algo25Dao.clearAll() }
        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT secret key WHEN getSecretKey is invoked`() = runTest {
        val encryptedSK = "encryptedSecretKey".toByteArray()
        val decryptedSK = byteArrayOf(1, 2, 3)
        coEvery { algo25Dao.get("address") } returns Algo25Entity("address", encryptedSK)
        coEvery { aesPlatformManager.decryptByteArray(encryptedSK) } returns decryptedSK

        val result = sut.getSecretKey("address")

        assertEquals(decryptedSK, result)
    }

    @Test
    fun `EXPECT null WHEN getSecretKey is invoked with a non-existent address`() = runTest {
        coEvery { algo25Dao.get("non_existent_address") } returns null

        val result = sut.getSecretKey("non_existent_address")

        coVerify { algo25Dao.get("non_existent_address") }
        assertEquals(null, result)
    }
}

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

import com.algorand.common.account.local.data.database.dao.Algo25Dao
import com.algorand.common.account.local.data.database.model.Algo25Entity
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.common.account.local.data.mapper.model.Algo25Mapper
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.encryption.AddressEncryptionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class Algo25AccountRepositoryImplTest {

    private val algo25Dao: Algo25Dao = mockk()
    private val algo25EntityMapper: Algo25EntityMapper = mockk()
    private val algo25Mapper: Algo25Mapper = mockk()
    private val encryptionManager: AddressEncryptionManager = mockk()
    private val sut = Algo25AccountRepositoryImpl(
        algo25Dao,
        algo25EntityMapper,
        algo25Mapper,
        encryptionManager
    )

    @Test
    fun `EXPECT all accounts WHEN getAll is invoked`() = runTest {
        val entities = listOf(
            Algo25Entity("encryptedAddress1", "encryptedSecretKey1"),
            Algo25Entity("encryptedAddress2", "encryptedSecretKey2")
        )
        coEvery { algo25Dao.getAll() } returns entities
        coEvery { algo25Mapper(entities[0]) } returns LocalAccount.Algo25("address1", byteArrayOf(1, 2, 3))
        coEvery { algo25Mapper(entities[1]) } returns LocalAccount.Algo25("address2", byteArrayOf(4, 5, 6))

        val localAccounts = sut.getAll()

        val expectedReturnedList = listOf(
            LocalAccount.Algo25("address1", byteArrayOf(1, 2, 3)),
            LocalAccount.Algo25("address2", byteArrayOf(4, 5, 6))
        )
        coVerify { algo25Dao.getAll() }
        assertEquals(expectedReturnedList, localAccounts)
    }

    @Test
    fun `EXPECT account WHEN account was registered before`() = runTest {
        coEvery { algo25Mapper(Algo25Entity("encryptedAddress", "encryptedSecretKey")) }
            .returns(LocalAccount.Algo25("address", byteArrayOf(1, 2, 3)))
        coEvery { algo25Dao.get("encryptedAddress") } returns Algo25Entity("encryptedAddress", "encryptedSecretKey")
        coEvery { encryptionManager.encrypt("address") } returns "encryptedAddress"

        val localAccount = sut.getAccount("address")

        val expectedAccount = LocalAccount.Algo25("address", byteArrayOf(1, 2, 3))
        coVerify(exactly = 1) { algo25Dao.get("encryptedAddress") }
        assertEquals(expectedAccount, localAccount)
    }

    @Test
    fun `EXPECT account to be added to database WHEN addAccount is invoked`() = runTest {
        val account = LocalAccount.Algo25("address", byteArrayOf(1, 2, 3))
        val algo25Entity = Algo25Entity("encryptedAddress", "encryptedSecretKey")
        coEvery { algo25EntityMapper(account) } returns algo25Entity
        coEvery { algo25Dao.insert(algo25Entity) } returns Unit

        sut.addAccount(account)

        coVerify { algo25Dao.insert(algo25Entity) }
    }

    @Test
    fun `EXPECT account to be deleted from database  WHEN deleteAccount is invoked`() = runTest {
        val address = "address"
        val encryptedAddress = "encryptedAddress"
        coEvery { encryptionManager.encrypt(address) } returns encryptedAddress
        coEvery { algo25Dao.delete(encryptedAddress) } returns Unit

        sut.deleteAccount(address)

        coVerify { algo25Dao.delete(encryptedAddress) }
    }

    @Test
    fun `EXPECT all accounts to be deleted from database WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { algo25Dao.clearAll() } returns Unit

        sut.deleteAllAccounts()

        coVerify { algo25Dao.clearAll() }
    }

    @Test
    fun `EXPECT all accounts as flow WHEN getAllAsFlow is invoked`() {
        val entities = listOf(
            Algo25Entity("encryptedAddress1", "encryptedSecretKey1"),
            Algo25Entity("encryptedAddress2", "encryptedSecretKey2")
        )
        val localAccounts = listOf(
            LocalAccount.Algo25("address1", byteArrayOf(1, 2, 3)),
            LocalAccount.Algo25("address2", byteArrayOf(4, 5, 6))
        )
        coEvery { algo25Dao.getAllAsFlow() } returns flowOf(entities)
        coEvery { algo25Mapper(entities[0]) } returns localAccounts[0]
        coEvery { algo25Mapper(entities[1]) } returns localAccounts[1]

        val flow = sut.getAllAsFlow()

        runTest {
            val returnedList = flow.toList()
            assertEquals(listOf(localAccounts), returnedList)
        }
    }
}

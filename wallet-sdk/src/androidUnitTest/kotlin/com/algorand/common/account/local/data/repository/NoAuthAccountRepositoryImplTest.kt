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

import com.algorand.common.account.local.data.database.dao.NoAuthDao
import com.algorand.common.account.local.data.database.model.NoAuthEntity
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.common.account.local.data.mapper.model.NoAuthMapper
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

class NoAuthAccountRepositoryImplTest {

    private val noAuthDao: NoAuthDao = mockk()
    private val noAuthEntityMapper: NoAuthEntityMapper = mockk()
    private val noAuthMapper: NoAuthMapper = mockk()
    private val encryptionManager: AddressEncryptionManager = mockk()
    private val sut = NoAuthAccountRepositoryImpl(
        noAuthDao,
        noAuthEntityMapper,
        noAuthMapper,
        encryptionManager
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
    fun `EXPECT account WHEN account was registered before`() = runTest {
        coEvery { noAuthMapper(NoAuthEntity("encrypted_address")) } returns LocalAccount.NoAuth("address")
        coEvery { noAuthDao.get("encrypted_address") } returns NoAuthEntity("encrypted_address")
        coEvery { encryptionManager.encrypt("address") } returns "encrypted_address"

        val localAccount = sut.getAccount("address")

        val expectedAccount = LocalAccount.NoAuth("address")
        coVerify { noAuthDao.get("encrypted_address") }
        assertEquals(expectedAccount, localAccount)
    }

    @Test
    fun `EXPECT account to be added to database WHEN addAccount is invoked`() = runTest {
        val account = LocalAccount.NoAuth("address")
        val ledgerUsbEntity = NoAuthEntity("encryptedAddress")
        coEvery { noAuthEntityMapper(account) } returns ledgerUsbEntity
        coEvery { noAuthDao.insert(ledgerUsbEntity) } returns Unit

        sut.addAccount(account)

        coVerify { noAuthDao.insert(ledgerUsbEntity) }
    }

    @Test
    fun `EXPECT account to be deleted WHEN deleteAccount is invoked`() = runTest {
        coEvery { noAuthDao.delete("encryptedAddress") } returns Unit
        coEvery { encryptionManager.encrypt("address") } returns "encryptedAddress"

        sut.deleteAccount("address")

        coVerify { noAuthDao.delete("encryptedAddress") }
    }

    @Test
    fun `EXPECT all accounts to be deleted WHEN deleteAllAccounts is invoked`() = runTest {
        coEvery { noAuthDao.clearAll() } returns Unit

        sut.deleteAllAccounts()

        coVerify { noAuthDao.clearAll() }
    }

    @Test
    fun `EXPECT all accounts WHEN getAllAsFlow is invoked`() = runTest {
        val entities = listOf(NoAuthEntity("address1"), NoAuthEntity("address2"))
        coEvery { noAuthDao.getAllAsFlow() } returns flowOf(entities)
        coEvery { noAuthMapper(entities[0]) } returns LocalAccount.NoAuth("address1")
        coEvery { noAuthMapper(entities[1]) } returns LocalAccount.NoAuth("address2")

        val localAccounts = sut.getAllAsFlow().toList().first()

        val expectedReturnedList = listOf(LocalAccount.NoAuth("address1"), LocalAccount.NoAuth("address2"))
        coVerify { noAuthDao.getAllAsFlow() }
        assertEquals(expectedReturnedList, localAccounts)
    }
}

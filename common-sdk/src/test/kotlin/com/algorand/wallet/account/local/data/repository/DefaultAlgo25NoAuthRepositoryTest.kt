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

import com.algorand.wallet.account.local.data.database.dao.Algo25NoAuthDao
import com.algorand.wallet.account.local.data.database.model.Algo25Entity
import com.algorand.wallet.account.local.data.database.model.NoAuthEntity
import com.algorand.wallet.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultAlgo25NoAuthRepositoryTest {

    private val algo25NoAuthDao: Algo25NoAuthDao = mockk(relaxed = true)
    private val noAuthEntityMapper: NoAuthEntityMapper = mockk(relaxed = true)
    private val aesPlatformManager: AESPlatformManager = mockk(relaxed = true)

    private val sut = DefaultAlgo25NoAuthRepository(algo25NoAuthDao, noAuthEntityMapper, aesPlatformManager)

    @Test
    fun `EXPECT Algo25 accounts with invalid secret keys to be updated as NoAuth accounts`() = runTest {
        every { aesPlatformManager.decryptByteArray(VALID_ENCRYPTED_SECRET_KEY) } returns VALID_SECRET_KEY
        every { aesPlatformManager.decryptByteArray(EMPTY_ENCRYPTED_SECRET_KEY) } returns byteArrayOf()
        every { aesPlatformManager.decryptByteArray(DEFAULT_VALUE_ENCRYPTED_SECRET_KEY) } returns byteArrayOf(0)
        coEvery { noAuthEntityMapper(ADDRESS_2) } returns NO_AUTH_ENTITY_1
        coEvery { noAuthEntityMapper(ADDRESS_3) } returns NO_AUTH_ENTITY_2
        coEvery {
            algo25NoAuthDao.getAllAlgo25Entities()
        } returns listOf(VALID_ALGO_25_ENTITY, INVALID_ALGO_25_ENTITY_1, INVALID_ALGO_25_ENTITY_2)

        sut.updateInvalidAlgo25AccountsToNoAuth()

        coVerify(exactly = 1) {
            algo25NoAuthDao.updateAlgo25AccountsToNoAuthAccounts(listOf(NO_AUTH_ENTITY_1, NO_AUTH_ENTITY_2))
        }
    }

    private companion object {
        const val ADDRESS_1 = "address_1"
        const val ADDRESS_2 = "address_2"
        const val ADDRESS_3 = "address_3"

        val VALID_ENCRYPTED_SECRET_KEY = byteArrayOf(1, 2, 3)
        val VALID_SECRET_KEY = byteArrayOf(7, 8, 9)
        val VALID_ALGO_25_ENTITY = Algo25Entity(ADDRESS_1, VALID_ENCRYPTED_SECRET_KEY)

        val DEFAULT_VALUE_ENCRYPTED_SECRET_KEY = byteArrayOf(0)
        val EMPTY_ENCRYPTED_SECRET_KEY = byteArrayOf()
        val INVALID_ALGO_25_ENTITY_1 = Algo25Entity(ADDRESS_2, EMPTY_ENCRYPTED_SECRET_KEY)
        val INVALID_ALGO_25_ENTITY_2 = Algo25Entity(ADDRESS_3, DEFAULT_VALUE_ENCRYPTED_SECRET_KEY)

        val NO_AUTH_ENTITY_1 = NoAuthEntity(ADDRESS_2)
        val NO_AUTH_ENTITY_2 = NoAuthEntity(ADDRESS_3)
    }
}

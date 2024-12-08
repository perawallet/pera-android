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

package com.algorand.common.account.local.domain.usecase

import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.account.local.domain.repository.Bip39AccountRepository
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Test

class GetSecretKeyUseCaseTest {

    private val bip39AccountRepository: Bip39AccountRepository = mockk()
    private val algo25AccountRepository: Algo25AccountRepository = mockk()

    private val sutAlgo25 = GetSecretKeyAlgo25UseCase(algo25AccountRepository)
    private val sutBip39 = GetSecretKeyBip39UseCase(bip39AccountRepository)

    @Test
    fun `EXPECT secret key WHEN bip39 account is found`() = runTest {
        coEvery { bip39AccountRepository.getAccount(BIP_39_ADDRESS) } returns BIP_39_ACCOUNT
        val result = sutBip39(BIP_39_ADDRESS)
        assertTrue(result.contentEquals(BIP_39_ACCOUNT.secretKey))
    }

    @Test
    fun `EXPECT secret key WHEN algo25 account is found`() = runTest {
        coEvery { algo25AccountRepository.getAccount(ALGO_25_ADDRESS) } returns ALGO_25_ACCOUNT
        val result = sutAlgo25(ALGO_25_ADDRESS)
        assertTrue(result.contentEquals(ALGO_25_ACCOUNT.secretKey))
    }

    @Test
    fun `EXPECT null WHEN Bip39 account is not found`() = runTest {
        coEvery { bip39AccountRepository.getAccount(BIP_39_ADDRESS) } returns null

        val result = sutBip39(BIP_39_ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN Algo25 account is not found`() = runTest {
        coEvery { algo25AccountRepository.getAccount(ALGO_25_ADDRESS) } returns null

        val result = sutAlgo25(ALGO_25_ADDRESS)

        assertNull(result)
    }

    companion object {
        private const val BIP_39_ADDRESS = "ADDRESS_1"
        private const val ALGO_25_ADDRESS = "ADDRESS_2"
        private val BIP_39_ACCOUNT = peraFixture<LocalAccount.Bip39>().copy(address = BIP_39_ADDRESS)
        private val ALGO_25_ACCOUNT = peraFixture<LocalAccount.Algo25>().copy(address = ALGO_25_ADDRESS)
    }
}

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

package com.algorand.common.account.local.usecase

import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.account.local.domain.usecase.GetSecretKeyUseCase
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Test

class GetSecretKeyUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mockk()

    private val sut = GetSecretKeyUseCase(algo25AccountRepository)

    @Test
    fun `EXPECT secret key WHEN account is found`() = runTest {
        coEvery { algo25AccountRepository.getAccount(ALGO_25_ADDRESS) } returns ALGO_25_ACCOUNT

        val result = sut(ALGO_25_ADDRESS)

        assertTrue(result.contentEquals(ALGO_25_ACCOUNT.secretKey))
    }

    @Test
    fun `EXPECT null WHEN account is not found`() = runTest {
        coEvery { algo25AccountRepository.getAccount(ALGO_25_ADDRESS) } returns null

        val result = sut(ALGO_25_ADDRESS)

        assertNull(result)
    }

    companion object {
        private const val ALGO_25_ADDRESS = "ADDRESS_1"

        private val ALGO_25_ACCOUNT = peraFixture<LocalAccount.Algo25>().copy(address = ALGO_25_ADDRESS)
    }
}

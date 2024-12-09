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

class GetSecretKeyBip39UseCaseTest {

    private val bip39AccountRepository: Bip39AccountRepository = mockk()

    private val sut = GetSecretKeyBip39UseCase(bip39AccountRepository)

    @Test
    fun `EXPECT secret key WHEN bip39 account is found`() = runTest {
        coEvery { bip39AccountRepository.getAccount(BIP_39_ADDRESS) } returns BIP_39_ACCOUNT
        val result = sut9(BIP_39_ADDRESS)
        assertTrue(result.contentEquals(BIP_39_ACCOUNT.secretKey))
    }

    @Test
    fun `EXPECT null WHEN Bip39 account is not found`() = runTest {
        coEvery { bip39AccountRepository.getAccount(BIP_39_ADDRESS) } returns null

        val result = sut(BIP_39_ADDRESS)

        assertNull(result)
    }

    companion object {
        private const val BIP_39_ADDRESS = "ADDRESS_1"
        private val BIP_39_ACCOUNT = peraFixture<LocalAccount.Bip39>().copy(address = BIP_39_ADDRESS)
    }
}

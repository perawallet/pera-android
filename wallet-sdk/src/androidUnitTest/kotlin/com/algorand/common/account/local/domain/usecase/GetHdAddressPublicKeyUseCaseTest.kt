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
import com.algorand.common.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Test

class GetHdAddressPublicKeyUseCaseTest {

    private val hdKeyAccountRepository: HdKeyAccountRepository = mockk()

    private val sut = GetHdAddressPublicKeyUseCase(hdKeyAccountRepository)

//    @Test
//    fun `EXPECT seed WHEN hd account is found`() = runTest {
//        coEvery { hdKeyAccountRepository.getAccount(HD_ADDRESS) } returns HD_ACCOUNT
//        val result = sut(HD_ADDRESS)
//        assertTrue(result.contentEquals(HD_ACCOUNT.seedId))
//    }

    @Test
    fun `EXPECT null WHEN hd account is not found`() = runTest {
        coEvery { hdKeyAccountRepository.getAccount(HD_ADDRESS) } returns null

        val result = sut(HD_ADDRESS)

        assertNull(result)
    }

    companion object {
        private const val HD_ADDRESS = "ADDRESS_1"
        private val HD_ACCOUNT = peraFixture<LocalAccount.HdKey>().copy(address = HD_ADDRESS)
    }
}

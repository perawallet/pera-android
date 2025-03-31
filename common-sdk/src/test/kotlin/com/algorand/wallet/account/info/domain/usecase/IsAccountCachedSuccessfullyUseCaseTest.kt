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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.runTest

class IsAccountCachedSuccessfullyUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mock()

    private val sut = IsAccountCachedSuccessfullyUseCase(accountInformationRepository)

    @Test
    fun `EXPECT true WHEN address is not failed addresses list`() = runTest {
        whenever(accountInformationRepository.getFailedAccountInformation())
            .thenReturn(SUCCESSFULLY_CACHED_ADDRESSES)

        val result = sut("anotherAddress")

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN address is in failed addresses list`() = runTest {
        whenever(accountInformationRepository.getFailedAccountInformation())
            .thenReturn(SUCCESSFULLY_CACHED_ADDRESSES)

        val result = sut(ADDRESS)

        assertTrue(!result)
    }

    private companion object {
        private const val ADDRESS = "address"
        private val SUCCESSFULLY_CACHED_ADDRESSES = listOf(ADDRESS)
    }
}

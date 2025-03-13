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

import com.algorand.wallet.account.info.data.mapper.model.AccountFastLookupMapper
import com.algorand.wallet.account.info.data.model.AccountFastLookupResponse
import com.algorand.wallet.account.info.data.repository.AccountFastLookupRepository
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest

class GetAccountFastLookupUseCaseTest {

    private lateinit var sut: GetAccountFastLookupUseCase
    private val mockAccountFastLookupRepository = mockk<AccountFastLookupRepository>()
    private val mockAccountFastLookupMapper = mockk<AccountFastLookupMapper>()

    @Before
    fun setup() {
        sut = GetAccountFastLookupUseCase(
            mockAccountFastLookupRepository,
            mockAccountFastLookupMapper
        )
    }

    @Test
    fun `EXPECT mapped account fast lookup WHEN fetch is successful`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = mockk<AccountFastLookupResponse>()
        val expectedResult = mockk<AccountFastLookup>()

        coEvery { mockAccountFastLookupRepository.fetchAccountFastLookup(address) } returns PeraResult.Success(mockResponse)
        coEvery { mockAccountFastLookupMapper(mockResponse) } returns expectedResult

        val result = sut(address)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `EXPECT null WHEN fetch fails`() = runTest {
        val address = "TEST_ADDRESS"
        val exception = java.io.IOException("Network error")

        coEvery { mockAccountFastLookupRepository.fetchAccountFastLookup(address) } returns PeraResult.Error(exception)

        val result = sut(address)

        assertNull(result)
    }
}

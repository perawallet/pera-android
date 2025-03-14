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
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAccountFastLookupUseCaseTest {
    private var mockRepository: AccountFastLookupRepository = mockk()
    private var mockMapper: AccountFastLookupMapper = mockk()
    private var sut: GetAccountFastLookupUseCase = GetAccountFastLookupUseCase(
        mockRepository,
        mockMapper
    )

    @Test
    fun `EXPECT Success result WHEN repository returns valid data`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = mockk<AccountFastLookupResponse>()
        val expectedAccountFastLookup = mockk<AccountFastLookup>()
        val repositoryResult = PeraResult.Success(mockResponse)

        coEvery { mockRepository.fetchAccountFastLookup(address) } returns repositoryResult
        every { mockMapper.invoke(mockResponse) } returns expectedAccountFastLookup

        val result = sut(address)

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedAccountFastLookup, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT Error result WHEN repository throws exception`() = runTest {
        val address = "TEST_ADDRESS"
        val expectedException = IOException("Network error")

        coEvery { mockRepository.fetchAccountFastLookup(address) } throws expectedException

        val result = sut(address)

        assertTrue(result is PeraResult.Error)
        assertEquals(expectedException, (result as PeraResult.Error).exception)
    }

    @Test
    fun `EXPECT Error result WHEN repository returns error`() = runTest {
        // Given
        val address = "TEST_ADDRESS"
        val expectedException = IllegalArgumentException("Empty AccountFastLookupResponse object")
        val repositoryResult = PeraResult.Error(expectedException)

        coEvery { mockRepository.fetchAccountFastLookup(address) } returns repositoryResult

        val result = sut(address)

        assertTrue(result is PeraResult.Error)
        assertTrue((result as PeraResult.Error).exception is IllegalArgumentException)
        assertEquals("Empty AccountFastLookupResponse object", result.exception.message)
    }
}

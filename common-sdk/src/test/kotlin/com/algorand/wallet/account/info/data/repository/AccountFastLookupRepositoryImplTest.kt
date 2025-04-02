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

package com.algorand.wallet.account.info.data.repository

import com.algorand.wallet.account.info.data.mapper.model.AccountFastLookupMapper
import com.algorand.wallet.account.info.data.model.AccountFastLookupResponse
import com.algorand.wallet.account.info.data.service.AccountFastLookupApiService
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AccountFastLookupRepositoryImplTest {

    private val mockApi = mockk<AccountFastLookupApiService>()
    private val accountFastLookupMapper = mockk<AccountFastLookupMapper>()
    private var sut: AccountFastLookupRepositoryImpl = AccountFastLookupRepositoryImpl(
        mockApi,
        accountFastLookupMapper
    )

    @Test
    fun `EXPECT success result WHEN api returns valid response`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponseData = mockk<AccountFastLookupResponse>()
        val account = AccountFastLookup(
            algoValue = BigDecimal("1000000.00"),
            usdValue = BigDecimal("150.25"),
            accountExists = true
        )
        coEvery { mockApi.getAccountFastLookup(address) } returns Response.success(mockResponseData)
        every { accountFastLookupMapper.invoke(response = mockResponseData) } returns account

        val result = sut.fetchAccountFastLookup(address)

        assertTrue(result is PeraResult.Success)
        assertEquals(account, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error result WHEN api throws exception`() = runTest {
        val address = "TEST_ADDRESS"
        val exception = IOException()

        coEvery { mockApi.getAccountFastLookup(address) } throws exception

        val result = sut.fetchAccountFastLookup(address)

        assertTrue(result is PeraResult.Error)
        assertTrue((result as PeraResult.Error).exception is IOException)
    }

    @Test
    fun `EXPECT error result with code WHEN api returns error code`() = runTest {
        val address = "TEST_ADDRESS"
        val errorCode = 404
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            mockApi.getAccountFastLookup(address)
        } returns Response.error(errorCode, errorBody)

        val result = sut.fetchAccountFastLookup(address)

        assertTrue(result is PeraResult.Error)
        assertEquals(errorCode, (result as PeraResult.Error).code)
    }
}

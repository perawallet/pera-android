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

import com.algorand.wallet.account.info.data.model.AccountFastLookupResponse
import com.algorand.wallet.account.info.data.service.AccountFastLookupApiService
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import kotlinx.coroutines.test.runTest


class AccountFastLookupFetchHelperImplTest {

    private lateinit var sut: AccountFastLookupFetchHelperImpl
    private val mockApi = mockk<AccountFastLookupApiService>()

    @Before
    fun setup() {
        sut = AccountFastLookupFetchHelperImpl(mockApi)
    }

    @Test
    fun `EXPECT success result WHEN api returns valid response`() = runTest {
        val address = "TEST_ADDRESS"
        val mockResponse = AccountFastLookupResponse(
            algoValue = "1000000",
            usdValue = "150.25",
            calculationType = "estimated",
            accountExists = true
        )
        val responseWrapper = Response.success(mockResponse)
        coEvery { mockApi.getAccountFastLookup(address) } returns responseWrapper

        val result = sut.fetchAccountFastLookup(address)

        assertTrue(result is PeraResult.Success)
        assertEquals(mockResponse, (result as PeraResult.Success).data)
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
        val errorResponse = Response.error<AccountFastLookupResponse>(
            errorCode,
            "".toResponseBody("application/json".toMediaTypeOrNull())
        )
        coEvery { mockApi.getAccountFastLookup(address) } returns errorResponse

        val result = sut.fetchAccountFastLookup(address)

        assertTrue(result is PeraResult.Error)
    }
}
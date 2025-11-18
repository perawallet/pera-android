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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAccountFastLookupBatchUseCaseTest {

    private val getAccountFastLookup: GetAccountFastLookup = mockk()

    private val sut = GetAccountFastLookupBatchUseCase(getAccountFastLookup)

    @Test
    fun `EXPECT successful account fast lookup map`(): TestResult = runTest {
        coEvery { getAccountFastLookup(ADDRESS_1) } returns PeraResult.Success(ACCOUNT_FAST_LOOKUP_1)
        coEvery { getAccountFastLookup(ADDRESS_2) } returns PeraResult.Success(ACCOUNT_FAST_LOOKUP_2)
        coEvery { getAccountFastLookup(ADDRESS_3) } returns PeraResult.Error(Exception())

        val result = sut(ADDRESSES)

        val expected = mapOf(
            ADDRESS_1 to ACCOUNT_FAST_LOOKUP_1,
            ADDRESS_2 to ACCOUNT_FAST_LOOKUP_2,
            ADDRESS_3 to null
        )
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS_1 = "address1"
        const val ADDRESS_2 = "address2"
        const val ADDRESS_3 = "address3"

        val ACCOUNT_FAST_LOOKUP_1 = peraFixture<AccountFastLookup>()
        val ACCOUNT_FAST_LOOKUP_2 = peraFixture<AccountFastLookup>()

        val ADDRESSES = listOf(ADDRESS_1, ADDRESS_2, ADDRESS_3)
    }
}

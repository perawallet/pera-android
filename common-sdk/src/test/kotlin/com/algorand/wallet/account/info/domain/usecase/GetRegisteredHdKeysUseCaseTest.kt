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
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRegisteredHdKeysUseCaseTest {

    private val getAccountFastLookup: GetAccountFastLookup = mockk {
        coEvery { invoke(address = "address") } returns PeraResult.Error(Exception())
    }
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress = mockk {
        coEvery { invoke(address = "address") } returns false
    }
    private val peraBip39Sdk: PeraBip39Sdk = mockk {
        every { generateHdKeyAddress(ENTROPY, any(), any(), any()) } returns "address"
    }

    private val sut = GetRegisteredHdKeysUseCase(
        getAccountFastLookup = getAccountFastLookup,
        isThereAnyAccountWithAddress = isThereAnyAccountWithAddress,
        peraBip39Sdk = peraBip39Sdk
    )

    @Test
    fun `EXPECT empty list WHEN fast lookup fails`() = runTest {
        every { peraBip39Sdk.generateHdKeyAddress(ENTROPY, 0, 0, 0) } returns ADDRESS
        coEvery { getAccountFastLookup(ADDRESS) } returns PeraResult.Error(Exception())

        val result = sut(ENTROPY)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT empty list WHEN accounts are not exists`() = runTest {
        every { peraBip39Sdk.generateHdKeyAddress(ENTROPY, 0, 0, 0) } returns ADDRESS
        val fastLookup = ACCOUNT_FAST_LOOKUP.copy(accountExists = false)
        coEvery { getAccountFastLookup(ADDRESS) } returns PeraResult.Success(fastLookup)

        val result = sut(ENTROPY)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT registered hd keys WHEN there are accounts`() = runTest {
        every { peraBip39Sdk.generateHdKeyAddress(ENTROPY, 0, 0, 0) } returns ADDRESS
        val fastLookup = ACCOUNT_FAST_LOOKUP.copy(accountExists = true)
        coEvery { getAccountFastLookup(ADDRESS) } returns PeraResult.Success(fastLookup)
        coEvery { isThereAnyAccountWithAddress(ADDRESS) } returns true

        val result = sut(ENTROPY)

        val expected = listOf(REGISTERED_HD_KEY)
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS = "address_1"
        val ACCOUNT_FAST_LOOKUP = peraFixture<AccountFastLookup>()
        val ENTROPY = byteArrayOf(1, 2, 3)
        val REGISTERED_HD_KEY = peraFixture<RegisteredHdKey>().copy(
            address = ADDRESS,
            algoValue = BigDecimal.ZERO,
            usdValue = BigDecimal.ZERO,
            accountExists = true,
            account = 0,
            change = 0,
            keyIndex = 0,
            isImportedToDB = true
        )
    }
}

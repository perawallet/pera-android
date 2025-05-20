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
import com.algorand.wallet.account.info.domain.mapper.HdAccountAddressMapper
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount.HdAccountAddress
import com.algorand.wallet.account.info.domain.model.HdKeyDetail
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetActiveHdAccountsUseCaseTest {

    private val peraBip39Sdk: PeraBip39Sdk = mockk {
        every { generateHdKeyAddress(ENTROPY, 0, 0, 0) } returns ACC_1_ADDR_1
        every { generateHdKeyAddress(ENTROPY, 0, 0, 1) } returns ACC_1_ADDR_2
        every { generateHdKeyAddress(ENTROPY, 0, 0, 2) } returns ACC_1_ADDR_3
        every { generateHdKeyAddress(ENTROPY, 0, 0, 3) } returns ACC_1_ADDR_4
        every { generateHdKeyAddress(ENTROPY, 0, 0, 4) } returns ACC_1_ADDR_5
        every { generateHdKeyAddress(ENTROPY, 1, 0, 0) } returns ACC_2_ADDR_1
        every { generateHdKeyAddress(ENTROPY, 1, 0, 1) } returns ACC_2_ADDR_2
        every { generateHdKeyAddress(ENTROPY, 1, 0, 2) } returns ACC_2_ADDR_3
        every { generateHdKeyAddress(ENTROPY, 1, 0, 3) } returns ACC_2_ADDR_4
        every { generateHdKeyAddress(ENTROPY, 1, 0, 4) } returns ACC_2_ADDR_5
    }
    private val getAccountFastLookupBatch: GetAccountFastLookupBatch = mockk()
    private val hdAccountAddressMapper: HdAccountAddressMapper = mockk()

    private val sut = GetActiveHdAccountsUseCase(
        peraBip39Sdk = peraBip39Sdk,
        getAccountFastLookupBatch = getAccountFastLookupBatch,
        hdAccountAddressMapper = hdAccountAddressMapper
    )

    @Test
    fun `EXPECT empty list WHEN the first account addresses are closed or fast lookup null`() = runTest {
        val batchFastLookupResult = mapOf(
            ACC_1_ADDR_1 to ACC_1_ADDR_1_FAST_LOOKUP.copy(accountExists = false),
            ACC_1_ADDR_2 to ACC_1_ADDR_2_FAST_LOOKUP.copy(accountExists = false),
            ACC_1_ADDR_3 to null,
            ACC_1_ADDR_4 to ACC_1_ADDR_4_FAST_LOOKUP.copy(accountExists = false),
            ACC_1_ADDR_5 to ACC_1_ADDR_5_FAST_LOOKUP.copy(accountExists = false),
        )
        coEvery { getAccountFastLookupBatch(FIRST_ACCOUNT_ADDRESSES) } returns batchFastLookupResult

        val result = sut(ENTROPY)

        assert(result.isEmpty())
    }

    @Test
    fun `EXPECT active account list WHEN there is active accounts`() = runTest {
        val firstFastLookupResult = mapOf(
            ACC_1_ADDR_1 to ACC_1_ADDR_1_FAST_LOOKUP.copy(accountExists = false),
            ACC_1_ADDR_2 to ACC_1_ADDR_2_FAST_LOOKUP.copy(accountExists = false),
            ACC_1_ADDR_3 to ACC_1_ADDR_3_FAST_LOOKUP.copy(accountExists = false),
            ACC_1_ADDR_4 to ACC_1_ADDR_4_FAST_LOOKUP.copy(accountExists = true),
            ACC_1_ADDR_5 to ACC_1_ADDR_5_FAST_LOOKUP.copy(accountExists = false),
        )
        val acc1HdAccountAddresses = listOf(
            HdAccountAddress(ACC_1_ADDR_1, 0, 0, 0, ACC_1_ADDR_1_FAST_LOOKUP.copy(accountExists = false)),
            HdAccountAddress(ACC_1_ADDR_2, 0, 0, 1, ACC_1_ADDR_2_FAST_LOOKUP.copy(accountExists = false)),
            HdAccountAddress(ACC_1_ADDR_3, 0, 0, 2, ACC_1_ADDR_3_FAST_LOOKUP.copy(accountExists = false)),
            HdAccountAddress(ACC_1_ADDR_4, 0, 0, 3, ACC_1_ADDR_4_FAST_LOOKUP.copy(accountExists = true)),
            HdAccountAddress(ACC_1_ADDR_5, 0, 0, 4, ACC_1_ADDR_5_FAST_LOOKUP.copy(accountExists = false)),
        )
        coEvery { getAccountFastLookupBatch(FIRST_ACCOUNT_ADDRESSES) } returns firstFastLookupResult
        coEvery { getAccountFastLookupBatch(SECOND_ACCOUNT_ADDRESSES) } returns emptyMap()
        every { hdAccountAddressMapper(ACC_1_HD_KEY_DETAILS, firstFastLookupResult) } returns acc1HdAccountAddresses

        val result = sut(ENTROPY)

        val expected = listOf(ActiveHdAccount(accountIndex = 0, ENTROPY, acc1HdAccountAddresses))
        assertEquals(expected, result)
    }

    private companion object {
        val ENTROPY = byteArrayOf(1, 2, 3)

        const val ACC_1_ADDR_1 = "acc_1_addr_1"
        const val ACC_1_ADDR_2 = "acc_1_addr_2"
        const val ACC_1_ADDR_3 = "acc_1_addr_3"
        const val ACC_1_ADDR_4 = "acc_1_addr_4"
        const val ACC_1_ADDR_5 = "acc_1_addr_5"
        const val ACC_2_ADDR_1 = "acc_2_addr_1"
        const val ACC_2_ADDR_2 = "acc_2_addr_2"
        const val ACC_2_ADDR_3 = "acc_2_addr_3"
        const val ACC_2_ADDR_4 = "acc_2_addr_4"
        const val ACC_2_ADDR_5 = "acc_2_addr_5"

        val FIRST_ACCOUNT_ADDRESSES = listOf(ACC_1_ADDR_1, ACC_1_ADDR_2, ACC_1_ADDR_3, ACC_1_ADDR_4, ACC_1_ADDR_5)

        val ACC_1_ADDR_1_FAST_LOOKUP = peraFixture<AccountFastLookup>()
        val ACC_1_ADDR_2_FAST_LOOKUP = peraFixture<AccountFastLookup>()
        val ACC_1_ADDR_3_FAST_LOOKUP = peraFixture<AccountFastLookup>()
        val ACC_1_ADDR_4_FAST_LOOKUP = peraFixture<AccountFastLookup>()
        val ACC_1_ADDR_5_FAST_LOOKUP = peraFixture<AccountFastLookup>()

        val ACC_1_HD_KEY_DETAILS = listOf(
            HdKeyDetail(ACC_1_ADDR_1, 0, 0, 0),
            HdKeyDetail(ACC_1_ADDR_2, 0, 0, 1),
            HdKeyDetail(ACC_1_ADDR_3, 0, 0, 2),
            HdKeyDetail(ACC_1_ADDR_4, 0, 0, 3),
            HdKeyDetail(ACC_1_ADDR_5, 0, 0, 4)
        )

        val SECOND_ACCOUNT_ADDRESSES = listOf(ACC_2_ADDR_1, ACC_2_ADDR_2, ACC_2_ADDR_3, ACC_2_ADDR_4, ACC_2_ADDR_5)
    }
}

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
import com.algorand.wallet.account.info.domain.model.HdKeyDetail
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetActiveHdAccountAddressesUseCaseTest {

    private val peraBip39Sdk: PeraBip39Sdk = mockk {
        every { generateHdKeyAddress(ENTROPY, 0, 0, 5) } returns ADDR_6
        every { generateHdKeyAddress(ENTROPY, 0, 0, 6) } returns ADDR_7
        every { generateHdKeyAddress(ENTROPY, 0, 0, 7) } returns ADDR_8
        every { generateHdKeyAddress(ENTROPY, 0, 0, 8) } returns ADDR_9
        every { generateHdKeyAddress(ENTROPY, 0, 0, 9) } returns ADDR_10
        every { generateHdKeyAddress(ENTROPY, 0, 0, 10) } returns ADDR_11
        every { generateHdKeyAddress(ENTROPY, 0, 0, 11) } returns ADDR_12
        every { generateHdKeyAddress(ENTROPY, 0, 0, 12) } returns ADDR_13
        every { generateHdKeyAddress(ENTROPY, 0, 0, 13) } returns ADDR_14
        every { generateHdKeyAddress(ENTROPY, 0, 0, 14) } returns ADDR_15
    }
    private val getAccountFastLookupBatch: GetAccountFastLookupBatch = mockk()
    private val hdAccountAddressMapper: HdAccountAddressMapper = mockk()

    private val sut = GetActiveHdAccountAddressesUseCase(
        peraBip39Sdk = peraBip39Sdk,
        getAccountFastLookupBatch = getAccountFastLookupBatch,
        hdAccountAddressMapper = hdAccountAddressMapper
    )

    @Test
    fun `EXPECT active addresses until whole batch is closed`() = runTest {
        coEvery { getAccountFastLookupBatch(SECOND_BATCH_ADDRESSES) } returns SECOND_BATCH_FAST_LOOKUP
        coEvery { getAccountFastLookupBatch(THIRD_BATCH_ADDRESSES) } returns THIRD_BATCH_FAST_LOOKUP

        val hdKeyDetails = listOf(
            ADDR_6_HD_KEY_DETAIL,
            ADDR_7_HD_KEY_DETAIL,
            ADDR_8_HD_KEY_DETAIL,
            ADDR_9_HD_KEY_DETAIL,
            ADDR_10_HD_KEY_DETAIL
        )
        every { hdAccountAddressMapper(hdKeyDetails, SECOND_BATCH_FAST_LOOKUP) } returns SECOND_BATCH_HD_ACCOUNT_ADDRESS

        val result = sut(ACTIVE_HD_ACCOUNT)

        val expected = listOf(
            FIRST_BATCH_HD_ACCOUNT_ADDRESS,
            SECOND_BATCH_HD_ACCOUNT_ADDRESS
        ).flatten()
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDR_6 = "acc_1_addr_6"
        const val ADDR_7 = "acc_1_addr_7"
        const val ADDR_8 = "acc_1_addr_8"
        const val ADDR_9 = "acc_1_addr_9"
        const val ADDR_10 = "acc_1_addr_10"
        const val ADDR_11 = "acc_1_addr_11"
        const val ADDR_12 = "acc_1_addr_12"
        const val ADDR_13 = "acc_1_addr_13"
        const val ADDR_14 = "acc_1_addr_14"
        const val ADDR_15 = "acc_1_addr_15"

        val ENTROPY = byteArrayOf(1, 2, 3)

        val ADDR_6_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = true)
        val ADDR_7_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = true)
        val ADDR_8_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = true)
        val ADDR_9_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = true)
        val ADDR_10_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = true)

        val ADDR_11_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = false)
        val ADDR_12_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = false)
        val ADDR_13_FAST_LOOKUP: AccountFastLookup? = null
        val ADDR_14_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = false)
        val ADDR_15_FAST_LOOKUP = peraFixture<AccountFastLookup>().copy(accountExists = false)

        val ADDR_6_HD_KEY_DETAIL = HdKeyDetail(algoAddress = ADDR_6, accountIndex = 0, changeIndex = 0, keyIndex = 5)
        val ADDR_7_HD_KEY_DETAIL = HdKeyDetail(algoAddress = ADDR_7, accountIndex = 0, changeIndex = 0, keyIndex = 6)
        val ADDR_8_HD_KEY_DETAIL = HdKeyDetail(algoAddress = ADDR_8, accountIndex = 0, changeIndex = 0, keyIndex = 7)
        val ADDR_9_HD_KEY_DETAIL = HdKeyDetail(algoAddress = ADDR_9, accountIndex = 0, changeIndex = 0, keyIndex = 8)
        val ADDR_10_HD_KEY_DETAIL = HdKeyDetail(algoAddress = ADDR_10, accountIndex = 0, changeIndex = 0, keyIndex = 9)

        val SECOND_BATCH_ADDRESSES = listOf(ADDR_6, ADDR_7, ADDR_8, ADDR_9, ADDR_10)
        val SECOND_BATCH_FAST_LOOKUP = mapOf(
            ADDR_6 to ADDR_6_FAST_LOOKUP,
            ADDR_7 to ADDR_7_FAST_LOOKUP,
            ADDR_8 to ADDR_8_FAST_LOOKUP,
            ADDR_9 to ADDR_9_FAST_LOOKUP,
            ADDR_10 to ADDR_10_FAST_LOOKUP
        )

        val THIRD_BATCH_ADDRESSES = listOf(ADDR_11, ADDR_12, ADDR_13, ADDR_14, ADDR_15)
        val THIRD_BATCH_FAST_LOOKUP = mapOf(
            ADDR_11 to ADDR_11_FAST_LOOKUP,
            ADDR_12 to ADDR_12_FAST_LOOKUP,
            ADDR_13 to ADDR_13_FAST_LOOKUP,
            ADDR_14 to ADDR_14_FAST_LOOKUP,
            ADDR_15 to ADDR_15_FAST_LOOKUP
        )

        val FIRST_BATCH_HD_ACCOUNT_ADDRESS = listOf(
            ActiveHdAccount.HdAccountAddress(address = "add1", 0, 0, 0, fastLookup = peraFixture()),
            ActiveHdAccount.HdAccountAddress(address = "add2", 0, 0, 1, fastLookup = peraFixture()),
            ActiveHdAccount.HdAccountAddress(address = "add3", 0, 0, 2, fastLookup = peraFixture()),
            ActiveHdAccount.HdAccountAddress(address = "add4", 0, 0, 3, fastLookup = peraFixture()),
            ActiveHdAccount.HdAccountAddress(address = "add5", 0, 0, 4, fastLookup = peraFixture()),
        )

        val SECOND_BATCH_HD_ACCOUNT_ADDRESS = listOf(
            ActiveHdAccount.HdAccountAddress(address = ADDR_6, 0, 0, 5, ADDR_6_FAST_LOOKUP),
            ActiveHdAccount.HdAccountAddress(address = ADDR_7, 0, 0, 6, ADDR_7_FAST_LOOKUP),
            ActiveHdAccount.HdAccountAddress(address = ADDR_8, 0, 0, 7, ADDR_8_FAST_LOOKUP),
            ActiveHdAccount.HdAccountAddress(address = ADDR_9, 0, 0, 8, ADDR_9_FAST_LOOKUP),
            ActiveHdAccount.HdAccountAddress(address = ADDR_10, 0, 0, 9, ADDR_10_FAST_LOOKUP),
        )

        val ACTIVE_HD_ACCOUNT = ActiveHdAccount(
            accountIndex = 0,
            entropy = ENTROPY,
            firstBatchHdAccountAddress = FIRST_BATCH_HD_ACCOUNT_ADDRESS
        )
    }
}

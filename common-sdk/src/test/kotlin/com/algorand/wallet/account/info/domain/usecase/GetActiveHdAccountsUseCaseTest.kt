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

import com.algorand.wallet.account.info.domain.mapper.HdAccountAddressMapper
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39Wallet
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetActiveHdAccountsUseCaseTest {

    private val bip39Wallet: Bip39Wallet = mockk(relaxed = true)
    private val bip39WalletProvider: Bip39WalletProvider = mockk {
        every { getBip39Wallet(ENTROPY) } returns bip39Wallet
    }
    private val getAccountFastLookupBatch: GetAccountFastLookupBatch = mockk()
    private val hdAccountAddressMapper: HdAccountAddressMapper = mockk()

    private val sut = GetActiveHdAccountsUseCase(
        bip39WalletProvider = bip39WalletProvider,
        getAccountFastLookupBatch = getAccountFastLookupBatch,
        hdAccountAddressMapper = hdAccountAddressMapper
    )

    @Test
    fun `EXPECT empty list WHEN the first 5 accounts addresses are closed or fast lookup null`() = runTest {
        val accountCount = 5
        val testHelper = GetActiveHdAccountsUseCaseTestHelper(accountCount, addressCount = 5)
        mockPeraBip39SdkGenerateHdKeyAddress(testHelper)
        val firstFiveAccountBatchFastLookupResults = (0 until accountCount).map {
            testHelper.getInactiveAccountFastLookupResult(it)
        }
        firstFiveAccountBatchFastLookupResults.forEachIndexed { index, fastLookupResult ->
            val addresses = testHelper.getAccountAddresses(accountIndex = index)
            coEvery { getAccountFastLookupBatch(addresses) } returns fastLookupResult
        }

        val result = sut(ENTROPY)

        assert(result.isEmpty())
    }

    @Test
    fun `EXPECT fifth account WHEN first four accounts are closed`() = runTest {
        val accountCount = 10
        val testHelper = GetActiveHdAccountsUseCaseTestHelper(accountCount, addressCount = 5)
        val inactiveAccounts0to4 = (0 until 4).map { testHelper.getInactiveAccountFastLookupResult(it) }
        val account5 = testHelper.getActiveAccountFastLookupResult(4)
        val inactiveAccounts5to10 = (5 until accountCount).map { testHelper.getInactiveAccountFastLookupResult(it) }
        val accountsFastLookupResults = inactiveAccounts0to4 + account5 + inactiveAccounts5to10
        mockGetAccountFastLookupBatch(testHelper, accountsFastLookupResults)
        mockPeraBip39SdkGenerateHdKeyAddress(testHelper)
        val account5HdAccountAddresses = testHelper.getHdAccountAddresses(
            accountIndex = 4,
            fastLookups = account5.values.toList()
        )
        every {
            hdAccountAddressMapper(testHelper.getHdKeyDetails(accountIndex = 4), account5)
        } returns account5HdAccountAddresses

        val result = sut(ENTROPY)

        val expected = listOf(ActiveHdAccount(accountIndex = 4, ENTROPY, account5HdAccountAddresses))
        assertEquals(expected, result)
        verifyGetAccountFastLookupBatchInvokeCount(testHelper)
    }

    @Test
    fun `EXPECT first 20 accounts WHEN 21, 22, 23, 24, 25 accounts are inactive`() = runTest {
        val accountCount = 25
        val testHelper = GetActiveHdAccountsUseCaseTestHelper(accountCount, addressCount = 5)
        val activeAccounts = (0 until 20).map { testHelper.getActiveAccountFastLookupResult(it) }
        val inactiveAccounts = (20 until 25).map { testHelper.getInactiveAccountFastLookupResult(it) }
        val accountsFastLookupResults = activeAccounts + inactiveAccounts
        mockGetAccountFastLookupBatch(testHelper, accountsFastLookupResults)
        mockPeraBip39SdkGenerateHdKeyAddress(testHelper)
        val activeHdAccountAddresses = activeAccounts.mapIndexed { index, fastLookup ->
            testHelper.getHdAccountAddresses(accountIndex = index, fastLookups = fastLookup.values.toList())
        }
        repeat(20) { accountIndex ->
            every {
                hdAccountAddressMapper(testHelper.getHdKeyDetails(accountIndex), activeAccounts[accountIndex])
            } returns activeHdAccountAddresses[accountIndex]
        }

        val result = sut(ENTROPY)

        val expected = activeAccounts.map {
            val accountIndex = activeAccounts.indexOf(it)
            ActiveHdAccount(accountIndex = accountIndex, ENTROPY, activeHdAccountAddresses[accountIndex])
        }
        assertEquals(expected, result)
        verifyGetAccountFastLookupBatchInvokeCount(testHelper)
    }

    private fun mockPeraBip39SdkGenerateHdKeyAddress(testHelper: GetActiveHdAccountsUseCaseTestHelper) {
        testHelper.getAccountIndexAndAddressesPair().forEach { (accountIndex, addresses) ->
            addresses.forEachIndexed { addressIndex, address ->
                every {
                    bip39Wallet.generateAddressLite(HdKeyAddressIndex(accountIndex, 0, addressIndex))
                } returns address
            }
        }
    }

    private fun mockGetAccountFastLookupBatch(
        testHelper: GetActiveHdAccountsUseCaseTestHelper,
        accountsFastLookupResults: List<Map<String, AccountFastLookup?>>
    ) {
        accountsFastLookupResults.forEachIndexed { index, fastLookupResult ->
            val addresses = testHelper.getAccountAddresses(accountIndex = index)
            coEvery { getAccountFastLookupBatch(addresses) } returns fastLookupResult
        }
    }

    private fun verifyGetAccountFastLookupBatchInvokeCount(testHelper: GetActiveHdAccountsUseCaseTestHelper) {
        testHelper.getAccountIndexAndAddressesPair().forEach { (_, addresses) ->
            coVerify(exactly = 1) { getAccountFastLookupBatch(addresses.map { it.address }) }
        }
    }

    private companion object {
        val ENTROPY = byteArrayOf(1, 2, 3)
    }
}

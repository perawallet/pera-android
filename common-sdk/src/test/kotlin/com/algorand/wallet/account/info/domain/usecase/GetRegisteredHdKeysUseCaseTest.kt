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
import com.algorand.wallet.account.info.domain.mapper.RegisteredHdKeyMapper
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount
import com.algorand.wallet.account.info.domain.model.ActiveHdAccount.HdAccountAddress
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRegisteredHdKeysUseCaseTest {

    private val getLocalAccountsAddresses: GetLocalAccountsAddresses = mockk()
    private val getActiveHdAccounts: GetActiveHdAccounts = mockk()
    private val getActiveHdAccountAddresses: GetActiveHdAccountAddresses = mockk()
    private val registeredHdKeyMapper: RegisteredHdKeyMapper = mockk()
    private val peraBip39Sdk: PeraBip39Sdk = mockk {
        every { generateHdKeyAddress(ENTROPY, 0, 0, 0) } returns FIRST_ADDRESS
    }

    private val sut = GetRegisteredHdKeysUseCase(
        getLocalAccountsAddresses = getLocalAccountsAddresses,
        getActiveHdAccounts = getActiveHdAccounts,
        getActiveHdAccountAddresses = getActiveHdAccountAddresses,
        registeredHdKeyMapper = registeredHdKeyMapper,
        peraBip39Sdk = peraBip39Sdk
    )

    @Test
    fun `EXPECT first registered hd key WHEN there is no active hd account`() = runTest {
        val registeredHdKey = peraFixture<RegisteredHdKey>()
        coEvery { getActiveHdAccounts(ENTROPY) } returns emptyList()
        coEvery { getLocalAccountsAddresses() } returns emptyList()
        coEvery {
            registeredHdKeyMapper(FIRST_HD_ACCOUNT_ADDRESS, fastLookupAccount = null, isAlreadyImported = false)
        } returns registeredHdKey

        val result = sut(ENTROPY)

        val expected = listOf(registeredHdKey)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT first registered hd key WHEN all addresses are filtered out`() = runTest {
        val registeredHdKey = peraFixture<RegisteredHdKey>()
        coEvery { getActiveHdAccounts(ENTROPY) } returns listOf(ACTIVE_HD_ACCOUNT_1, ACTIVE_HD_ACCOUNT_2)
        coEvery { getLocalAccountsAddresses() } returns listOf(ADDRESS_1, ADDRESS_2)
        val firstAccountAddress = FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS.copy(fastLookup = null)
        val secondAccountAddress = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS.copy(
            fastLookup = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.copy(accountExists = false)
        )
        coEvery { getActiveHdAccountAddresses(ACTIVE_HD_ACCOUNT_1) } returns listOf(firstAccountAddress)
        coEvery { getActiveHdAccountAddresses(ACTIVE_HD_ACCOUNT_2) } returns listOf(secondAccountAddress)
        coEvery {
            registeredHdKeyMapper(FIRST_HD_ACCOUNT_ADDRESS, fastLookupAccount = null, isAlreadyImported = false)
        } returns registeredHdKey

        val result = sut(ENTROPY)

        val expected = listOf(registeredHdKey)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT registered hd keys WHEN there are active hd accounts`() = runTest {
        coEvery { getActiveHdAccounts(ENTROPY) } returns listOf(ACTIVE_HD_ACCOUNT_1, ACTIVE_HD_ACCOUNT_2)
        coEvery { getLocalAccountsAddresses() } returns listOf(ADDRESS_1, ADDRESS_2)
        coEvery { getActiveHdAccountAddresses(ACTIVE_HD_ACCOUNT_1) } returns listOf(FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS)
        coEvery { getActiveHdAccountAddresses(ACTIVE_HD_ACCOUNT_2) } returns listOf(SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS)
        every { peraBip39Sdk.generateHdKeyAddress(ENTROPY, 0, 0, 0) } returns ADDRESS_1
        coEvery {
            registeredHdKeyMapper(
                FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS,
                FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP,
                isAlreadyImported = true
            )
        } returns FIRST_ACCOUNT_REGISTERED_HD_KEY
        coEvery {
            registeredHdKeyMapper(
                SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS,
                SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP,
                isAlreadyImported = true
            )
        } returns SECOND_ACCOUNT_REGISTERED_HD_KEY

        val result = sut(ENTROPY)

        val expected = listOf(FIRST_ACCOUNT_REGISTERED_HD_KEY, SECOND_ACCOUNT_REGISTERED_HD_KEY)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT addresses to be filtered out WHEN fast lookup is null or account is not exist`() = runTest {
        coEvery {
            getActiveHdAccounts(ENTROPY)
        } returns listOf(ACTIVE_HD_ACCOUNT_1, ACTIVE_HD_ACCOUNT_2, ACTIVE_HD_ACCOUNT_3)
        coEvery { getLocalAccountsAddresses() } returns listOf(ADDRESS_1, ADDRESS_2, ADDRESS_3)
        val firstAccountAddress = FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS.copy(fastLookup = null)
        val secondAccountAddress = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS.copy(
            fastLookup = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.copy(accountExists = false)
        )
        coEvery { getActiveHdAccountAddresses(ACTIVE_HD_ACCOUNT_1) } returns listOf(firstAccountAddress)
        coEvery { getActiveHdAccountAddresses(ACTIVE_HD_ACCOUNT_2) } returns listOf(secondAccountAddress)
        coEvery { getActiveHdAccountAddresses(ACTIVE_HD_ACCOUNT_3) } returns listOf(THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS)
        every { peraBip39Sdk.generateHdKeyAddress(ENTROPY, 0, 0, 0) } returns ADDRESS_1
        coEvery {
            registeredHdKeyMapper(
                THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS,
                THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP,
                isAlreadyImported = true
            )
        } returns THIRD_ACCOUNT_REGISTERED_HD_KEY

        val result = sut(ENTROPY)

        val expected = listOf(THIRD_ACCOUNT_REGISTERED_HD_KEY)
        assertEquals(expected, result)
    }

    private companion object {
        val ENTROPY = byteArrayOf(1, 2, 3)
        const val FIRST_ADDRESS = "first_address"
        const val ADDRESS_1 = "address_1"
        const val ADDRESS_2 = "address_2"
        const val ADDRESS_3 = "address_3"
        val FIRST_HD_ACCOUNT_ADDRESS = HdAccountAddress(
            address = FIRST_ADDRESS,
            accountIndex = 0,
            changeIndex = 0,
            keyIndex = 0,
            fastLookup = null
        )

        val FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP = peraFixture<AccountFastLookup>().copy(
            accountExists = true
        )
        val FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS = HdAccountAddress(
            address = ADDRESS_1,
            accountIndex = 0,
            changeIndex = 0,
            keyIndex = 0,
            fastLookup = FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP
        )
        val ACTIVE_HD_ACCOUNT_1 = ActiveHdAccount(
            accountIndex = 0,
            entropy = ENTROPY,
            firstBatchHdAccountAddress = listOf(FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS)
        )
        val FIRST_ACCOUNT_REGISTERED_HD_KEY = RegisteredHdKey(
            address = FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS.address,
            algoValue = FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.algoValue,
            usdValue = FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.usdValue,
            accountExists = FIRST_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.accountExists,
            account = 0,
            change = 0,
            keyIndex = 0,
            isImportedToDB = true
        )

        val SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP = peraFixture<AccountFastLookup>().copy(
            accountExists = true
        )
        val SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS = HdAccountAddress(
            address = ADDRESS_2,
            accountIndex = 1,
            changeIndex = 0,
            keyIndex = 0,
            fastLookup = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP
        )
        val ACTIVE_HD_ACCOUNT_2 = ActiveHdAccount(
            accountIndex = 1,
            entropy = ENTROPY,
            firstBatchHdAccountAddress = listOf(SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS)
        )

        val SECOND_ACCOUNT_REGISTERED_HD_KEY = RegisteredHdKey(
            address = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS.address,
            algoValue = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.algoValue,
            usdValue = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.usdValue,
            accountExists = SECOND_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.accountExists,
            account = 1,
            change = 0,
            keyIndex = 0,
            isImportedToDB = true
        )

        val THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP = peraFixture<AccountFastLookup>().copy(
            accountExists = true
        )
        val THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS = HdAccountAddress(
            address = ADDRESS_3,
            accountIndex = 2,
            changeIndex = 0,
            keyIndex = 0,
            fastLookup = THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP
        )
        val ACTIVE_HD_ACCOUNT_3 = ActiveHdAccount(
            accountIndex = 0,
            entropy = ENTROPY,
            firstBatchHdAccountAddress = listOf(THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS)
        )
        val THIRD_ACCOUNT_REGISTERED_HD_KEY = RegisteredHdKey(
            address = THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS.address,
            algoValue = THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.algoValue,
            usdValue = THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.usdValue,
            accountExists = THIRD_ACCOUNT_HD_ACCOUNT_ADDRESS_LOOKUP.accountExists,
            account = 1,
            change = 0,
            keyIndex = 0,
            isImportedToDB = true
        )
    }
}

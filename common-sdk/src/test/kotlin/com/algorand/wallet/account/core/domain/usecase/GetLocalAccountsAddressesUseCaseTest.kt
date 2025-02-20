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

package com.algorand.wallet.account.core.domain.usecase

import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.wallet.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.wallet.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddressesUseCase
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.wheneverBlocking

class GetLocalAccountsAddressesUseCaseTest {

    private val hdKeyAccountRepository: HdKeyAccountRepository = mock()
    private val algo25AccountRepository: Algo25AccountRepository = mock()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mock()
    private val noAuthAccountRepository: NoAuthAccountRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var sut: GetLocalAccountsAddressesUseCase

    @Before
    fun setUp() {
        sut = GetLocalAccountsAddressesUseCase(
            hdKeyAccountRepository,
            algo25AccountRepository,
            ledgerBleAccountRepository,
            noAuthAccountRepository,
            testDispatcher
        )
    }

    @Test
    fun `EXPECT all addresses combined from different repositories`() = runTest(testDispatcher) {
        val hdKeyAddresses = listOf("hdKey1", "hdKey2")
        val algo25Addresses = listOf("algo25-1")
        val ledgerBleAddresses = listOf("ledger-1", "ledger-2")
        val noAuthAddresses = listOf("noAuth1")

        wheneverBlocking { hdKeyAccountRepository.getAllAddresses() } doReturn hdKeyAddresses
        wheneverBlocking { algo25AccountRepository.getAllAddresses() } doReturn algo25Addresses
        wheneverBlocking { ledgerBleAccountRepository.getAllAddresses() } doReturn ledgerBleAddresses
        wheneverBlocking { noAuthAccountRepository.getAllAddresses() } doReturn noAuthAddresses

        val result = sut.invoke()

        val expected = hdKeyAddresses + algo25Addresses + ledgerBleAddresses + noAuthAddresses
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT empty list when all repositories return empty`() = runTest(testDispatcher) {
        wheneverBlocking { hdKeyAccountRepository.getAllAddresses() } doReturn emptyList()
        wheneverBlocking { algo25AccountRepository.getAllAddresses() } doReturn emptyList()
        wheneverBlocking { ledgerBleAccountRepository.getAllAddresses() } doReturn emptyList()
        wheneverBlocking { noAuthAccountRepository.getAllAddresses() } doReturn emptyList()

        val result = sut.invoke()

        assertEquals(emptyList<String>(), result)
    }
}

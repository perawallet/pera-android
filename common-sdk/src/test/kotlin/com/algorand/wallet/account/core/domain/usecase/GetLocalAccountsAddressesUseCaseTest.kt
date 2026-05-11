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

package com.algorand.wallet.account.core.domain.usecase

import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.wallet.account.local.domain.repository.JointAccountRepository
import com.algorand.wallet.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.wallet.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddressesUseCase
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class GetLocalAccountsAddressesUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val hdKeyAccountRepository: HdKeyAccountRepository = mockk()
    private val algo25AccountRepository: Algo25AccountRepository = mockk()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk()
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk()
    private val jointAccountRepository: JointAccountRepository = mockk()
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk {
        every { this@mockk.invoke(any()) } returns true
    }

    private val sut = GetLocalAccountsAddressesUseCase(
        hdKeyAccountRepository,
        algo25AccountRepository,
        ledgerBleAccountRepository,
        noAuthAccountRepository,
        jointAccountRepository,
        isFeatureToggleEnabled,
        testDispatcher
    )

    @Test
    fun `EXPECT all addresses combined WHEN all repositories have accounts`() = runTest(testDispatcher) {
        coEvery { hdKeyAccountRepository.getAllAddresses() } returns HD_KEY_ADDRESSES
        coEvery { algo25AccountRepository.getAllAddresses() } returns ALGO_25_ADDRESSES
        coEvery { ledgerBleAccountRepository.getAllAddresses() } returns LEDGER_BLE_ADDRESSES
        coEvery { noAuthAccountRepository.getAllAddresses() } returns NO_AUTH_ADDRESSES
        coEvery { jointAccountRepository.getAllAddresses() } returns JOINT_ADDRESSES

        val result = sut()

        val expectedAddresses = HD_KEY_ADDRESSES + ALGO_25_ADDRESSES +
            LEDGER_BLE_ADDRESSES + NO_AUTH_ADDRESSES + JOINT_ADDRESSES
        assertEquals(expectedAddresses.size, result.size)
        assertTrue(result.containsAll(expectedAddresses))
    }

    @Test
    fun `EXPECT empty list WHEN all repositories return empty`() = runTest(testDispatcher) {
        coEvery { hdKeyAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { algo25AccountRepository.getAllAddresses() } returns emptyList()
        coEvery { ledgerBleAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { noAuthAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { jointAccountRepository.getAllAddresses() } returns emptyList()

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT all repositories queried WHEN invoked`() = runTest(testDispatcher) {
        coEvery { hdKeyAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { algo25AccountRepository.getAllAddresses() } returns emptyList()
        coEvery { ledgerBleAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { noAuthAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { jointAccountRepository.getAllAddresses() } returns emptyList()

        sut()

        coVerify {
            hdKeyAccountRepository.getAllAddresses()
            algo25AccountRepository.getAllAddresses()
            ledgerBleAccountRepository.getAllAddresses()
            noAuthAccountRepository.getAllAddresses()
            jointAccountRepository.getAllAddresses()
        }
    }

    @Test
    fun `EXPECT partial addresses WHEN some repositories return empty`() = runTest(testDispatcher) {
        coEvery { hdKeyAccountRepository.getAllAddresses() } returns HD_KEY_ADDRESSES
        coEvery { algo25AccountRepository.getAllAddresses() } returns emptyList()
        coEvery { ledgerBleAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { noAuthAccountRepository.getAllAddresses() } returns emptyList()
        coEvery { jointAccountRepository.getAllAddresses() } returns JOINT_ADDRESSES

        val result = sut()

        val expectedAddresses = HD_KEY_ADDRESSES + JOINT_ADDRESSES
        assertEquals(expectedAddresses.size, result.size)
        assertTrue(result.containsAll(expectedAddresses))
    }

    private companion object {
        val HD_KEY_ADDRESSES = listOf("hdKey1", "hdKey2")
        val ALGO_25_ADDRESSES = listOf("algo25-1")
        val LEDGER_BLE_ADDRESSES = listOf("ledger-1", "ledger-2")
        val NO_AUTH_ADDRESSES = listOf("noAuth1")
        val JOINT_ADDRESSES = listOf("joint-1")
    }
}

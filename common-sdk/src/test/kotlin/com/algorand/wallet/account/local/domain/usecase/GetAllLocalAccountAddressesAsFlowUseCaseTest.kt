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

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.local.domain.model.LocalAccount.Algo25
import com.algorand.wallet.account.local.domain.model.LocalAccount.HdKey
import com.algorand.wallet.account.local.domain.model.LocalAccount.Joint
import com.algorand.wallet.account.local.domain.model.LocalAccount.LedgerBle
import com.algorand.wallet.account.local.domain.model.LocalAccount.NoAuth
import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.wallet.account.local.domain.repository.JointAccountRepository
import com.algorand.wallet.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.wallet.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class GetAllLocalAccountAddressesAsFlowUseCaseTest {

    private val hdKeyAccountRepository: HdKeyAccountRepository = mockk()
    private val algo25AccountRepository: Algo25AccountRepository = mockk()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk()
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk()
    private val jointAccountRepository: JointAccountRepository = mockk()
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk {
        every { this@mockk.invoke(any()) } returns true
    }

    private val sut = GetAllLocalAccountAddressesAsFlowUseCase(
        hdKeyAccountRepository,
        algo25AccountRepository,
        ledgerBleAccountRepository,
        noAuthAccountRepository,
        jointAccountRepository,
        isFeatureToggleEnabled
    )

    @Test
    fun `EXPECT empty list WHEN all repositories return empty list`() = runTest {
        setupEmptyRepositories()

        val result = sut().first()
        advanceUntilIdle()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT all account addresses WHEN there are local accounts`() = runTest {
        setupRepositoriesWithAccounts()

        val result = sut().first()
        advanceUntilIdle()

        val expectedAddresses = setOf(
            HD_ADDRESS,
            ALGO_25_ADDRESS,
            LEDGER_BLE_ADDRESS,
            NO_AUTH_ADDRESS,
            JOINT_ADDRESS
        )
        assertEquals(expectedAddresses.size, result.size)
        assertTrue(result.containsAll(expectedAddresses))
    }

    @Test
    fun `EXPECT partial addresses WHEN some repositories return empty`() = runTest {
        every { hdKeyAccountRepository.getAllAsFlow() } returns flowOf(listOf(HD_ACCOUNT))
        every { algo25AccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { ledgerBleAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { noAuthAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { jointAccountRepository.getAllAsFlow() } returns flowOf(listOf(JOINT_ACCOUNT))

        val result = sut().first()
        advanceUntilIdle()

        val expectedAddresses = setOf(HD_ADDRESS, JOINT_ADDRESS)
        assertEquals(expectedAddresses.size, result.size)
        assertTrue(result.containsAll(expectedAddresses))
    }

    @Test
    fun `EXPECT joint address included WHEN only joint account exists`() = runTest {
        every { hdKeyAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { algo25AccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { ledgerBleAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { noAuthAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { jointAccountRepository.getAllAsFlow() } returns flowOf(listOf(JOINT_ACCOUNT))

        val result = sut().first()
        advanceUntilIdle()

        assertEquals(1, result.size)
        assertTrue(result.contains(JOINT_ADDRESS))
    }

    private fun setupEmptyRepositories() {
        every { hdKeyAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { algo25AccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { ledgerBleAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { noAuthAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { jointAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
    }

    private fun setupRepositoriesWithAccounts() {
        every { hdKeyAccountRepository.getAllAsFlow() } returns flowOf(listOf(HD_ACCOUNT))
        every { algo25AccountRepository.getAllAsFlow() } returns flowOf(listOf(ALGO_25_ACCOUNT))
        every { ledgerBleAccountRepository.getAllAsFlow() } returns flowOf(listOf(LEDGER_BLE_ACCOUNT))
        every { noAuthAccountRepository.getAllAsFlow() } returns flowOf(listOf(NO_AUTH_ACCOUNT))
        every { jointAccountRepository.getAllAsFlow() } returns flowOf(listOf(JOINT_ACCOUNT))
    }

    private companion object {
        const val HD_ADDRESS = "address1"
        val HD_ACCOUNT = peraFixture<HdKey>().copy(algoAddress = HD_ADDRESS)

        const val ALGO_25_ADDRESS = "address2"
        val ALGO_25_ACCOUNT = peraFixture<Algo25>().copy(algoAddress = ALGO_25_ADDRESS)

        const val LEDGER_BLE_ADDRESS = "address3"
        val LEDGER_BLE_ACCOUNT = peraFixture<LedgerBle>().copy(algoAddress = LEDGER_BLE_ADDRESS)

        const val NO_AUTH_ADDRESS = "address4"
        val NO_AUTH_ACCOUNT = peraFixture<NoAuth>().copy(algoAddress = NO_AUTH_ADDRESS)

        const val JOINT_ADDRESS = "address5"
        val JOINT_ACCOUNT = peraFixture<Joint>().copy(algoAddress = JOINT_ADDRESS)
    }
}

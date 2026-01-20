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
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.wallet.account.local.domain.repository.HdSeedRepository
import com.algorand.wallet.account.local.domain.repository.JointAccountRepository
import com.algorand.wallet.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.wallet.account.local.domain.repository.NoAuthAccountRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class DeleteLocalAccountUseCaseTest {

    private val hdKeyAccountRepository: HdKeyAccountRepository = mockk(relaxed = true)
    private val algo25AccountRepository: Algo25AccountRepository = mockk(relaxed = true)
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk(relaxed = true)
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk(relaxed = true)
    private val jointAccountRepository: JointAccountRepository = mockk(relaxed = true)
    private val getAccountRegistrationType: GetAccountRegistrationType = mockk(relaxed = true)
    private val hdSeedRepository: HdSeedRepository = mockk(relaxed = true)

    private val deleteLocalAccount = DeleteLocalAccountUseCase(
        hdKeyAccountRepository,
        algo25AccountRepository,
        noAuthAccountRepository,
        ledgerBleAccountRepository,
        jointAccountRepository,
        getAccountRegistrationType,
        hdSeedRepository,
    )

    @Test
    fun `EXPECT ledger account to be removed WHEN account type is LedgerBle`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.LedgerBle

        deleteLocalAccount(ADDRESS)

        coVerify { ledgerBleAccountRepository.deleteAccount(ADDRESS) }
    }

    @Test
    fun `EXPECT algo25 account to be removed WHEN account type is Algo25`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.Algo25

        deleteLocalAccount(ADDRESS)

        coVerify { algo25AccountRepository.deleteAccount(ADDRESS) }
    }

    @Test
    fun `EXPECT noAuth account to be removed WHEN account type is NoAuth`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.NoAuth

        deleteLocalAccount(ADDRESS)

        coVerify { noAuthAccountRepository.deleteAccount(ADDRESS) }
    }

    @Test
    fun `EXPECT joint account to be removed WHEN account type is Joint`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.Joint

        deleteLocalAccount(ADDRESS)

        coVerify { jointAccountRepository.deleteAccount(ADDRESS) }
    }

    @Test
    fun `EXPECT hdKey account to be removed without seed WHEN its seed has other accounts`() = runTest {
        val hdKey = peraFixture<LocalAccount.HdKey>().copy(algoAddress = ADDRESS)
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.HdKey
        coEvery { hdKeyAccountRepository.getAccount(ADDRESS) } returns hdKey
        coEvery { hdKeyAccountRepository.getDerivedAddressCountOfSeed(hdKey.seedId) } returns 1

        deleteLocalAccount(ADDRESS)

        coVerify { hdKeyAccountRepository.deleteAccount(ADDRESS) }
        coVerify(exactly = 0) { hdSeedRepository.deleteHdSeed(hdKey.seedId) }
    }

    @Test
    fun `EXPECT hdKey account and its seed to be removed WHEN its seed does not have other accounts`() = runTest {
        val hdKey = peraFixture<LocalAccount.HdKey>().copy(algoAddress = ADDRESS)
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.HdKey
        coEvery { hdKeyAccountRepository.getAccount(ADDRESS) } returns hdKey
        coEvery { hdKeyAccountRepository.getDerivedAddressCountOfSeed(hdKey.seedId) } returns 0

        deleteLocalAccount(ADDRESS)

        coVerify { hdKeyAccountRepository.deleteAccount(ADDRESS) }
        coVerify { hdSeedRepository.deleteHdSeed(hdKey.seedId) }
    }

    @Test
    fun `EXPECT no deletion WHEN hdKey account not found`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.HdKey
        coEvery { hdKeyAccountRepository.getAccount(ADDRESS) } returns null

        deleteLocalAccount(ADDRESS)

        coVerify(exactly = 0) { hdKeyAccountRepository.deleteAccount(any()) }
        coVerify(exactly = 0) { hdSeedRepository.deleteHdSeed(any()) }
    }

    private companion object {
        const val ADDRESS = "address"
    }
}

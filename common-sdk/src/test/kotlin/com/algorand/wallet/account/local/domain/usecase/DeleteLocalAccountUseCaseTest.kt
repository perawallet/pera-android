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
    private val getAccountRegistrationType: GetAccountRegistrationType = mockk(relaxed = true)
    private val hdSeedRepository: HdSeedRepository = mockk(relaxed = true)

    private val deleteLocalAccount = DeleteLocalAccountUseCase(
        hdKeyAccountRepository,
        algo25AccountRepository,
        noAuthAccountRepository,
        ledgerBleAccountRepository,
        getAccountRegistrationType,
        hdSeedRepository,
    )

    @Test
    fun `EXPECT ledger account to be removed`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.LedgerBle

        deleteLocalAccount(ADDRESS)

        coVerify { ledgerBleAccountRepository.deleteAccount(ADDRESS) }
    }

    @Test
    fun `EXPECT algo25 account to be removed`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.Algo25

        deleteLocalAccount(ADDRESS)
    }

    @Test
    fun `EXPECT noAuth account to be removed`() = runTest {
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.NoAuth

        deleteLocalAccount(ADDRESS)
    }

    @Test
    fun `EXPECT hdKey account to be removed without seed WHEN its seed has other accounts`() = runTest {
        val hdKey = peraFixture<LocalAccount.HdKey>().copy(
            algoAddress = ADDRESS,
            seedId = 1
        )
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.HdKey
        coEvery { hdKeyAccountRepository.getAccount(ADDRESS) } returns hdKey
        coEvery { hdKeyAccountRepository.getDerivedAddressCountOfSeed(1) } returns 1

        deleteLocalAccount(ADDRESS)

        coVerify { hdKeyAccountRepository.deleteAccount(ADDRESS) }
        coVerify(exactly = 0) { hdSeedRepository.deleteHdSeed(1) }
    }

    @Test
    fun `EXPECT hdKey account and its seed to be removed WHEN its seed does not have other accounts`() = runTest {
        val hdKey = peraFixture<LocalAccount.HdKey>().copy(
            algoAddress = ADDRESS,
            seedId = 1
        )
        coEvery { getAccountRegistrationType(ADDRESS) } returns AccountRegistrationType.HdKey
        coEvery { hdKeyAccountRepository.getAccount(ADDRESS) } returns hdKey
        coEvery { hdKeyAccountRepository.getDerivedAddressCountOfSeed(1) } returns 0

        deleteLocalAccount(ADDRESS)

        coVerify { hdKeyAccountRepository.deleteAccount(ADDRESS) }
        coVerify { hdSeedRepository.deleteHdSeed(1) }
    }

    private companion object {
        const val ADDRESS = "address"
    }
}

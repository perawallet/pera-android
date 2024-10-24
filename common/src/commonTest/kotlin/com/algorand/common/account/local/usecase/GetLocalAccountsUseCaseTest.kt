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

package com.algorand.common.account.local.usecase

import com.algorand.common.account.local.model.LocalAccount
import com.algorand.common.account.local.repository.Algo25AccountRepository
import com.algorand.common.account.local.repository.LedgerBleAccountRepository
import com.algorand.common.account.local.repository.NoAuthAccountRepository
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetLocalAccountsUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mockk()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk()
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk()

    private val sut = GetLocalAccountsUseCase(
        algo25AccountRepository,
        ledgerBleAccountRepository,
        noAuthAccountRepository
    )

    @Test(timeout = 200L)
    fun `EXPECT local accounts and all calls to be made async`() = runTest {
        coEvery { algo25AccountRepository.getAll() } returns ALGO_25_ACCOUNTS
        coEvery { ledgerBleAccountRepository.getAll() } returns LEDGER_BLE_ACCOUNTS
        coEvery { noAuthAccountRepository.getAll() } returns NO_AUTH_ACCOUNTS

        val result = sut()

        val expected = ALGO_25_ACCOUNTS + LEDGER_BLE_ACCOUNTS + NO_AUTH_ACCOUNTS
        assertEquals(expected, result)
    }

    companion object {
        private val ALGO_25_ACCOUNTS = peraFixture<List<LocalAccount.Algo25>>()
        private val LEDGER_BLE_ACCOUNTS = peraFixture<List<LocalAccount.LedgerBle>>()
        private val NO_AUTH_ACCOUNTS = peraFixture<List<LocalAccount.NoAuth>>()
    }
}

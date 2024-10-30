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
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetLocalAccountsUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mockk()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk()
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk()
    private val coroutineDispatcher = UnconfinedTestDispatcher()

    private val sut = GetLocalAccountsUseCase(
        algo25AccountRepository,
        ledgerBleAccountRepository,
        noAuthAccountRepository,
        coroutineDispatcher
    )

    @Test(timeout = 5000L)
    fun `EXPECT local accounts and all calls to be made async`() = runTest(coroutineDispatcher) {
        coEvery { algo25AccountRepository.getAll() } coAnswers { delay(10000L); ALGO_25_ACCOUNTS }
        coEvery { ledgerBleAccountRepository.getAll() } coAnswers { delay(10000L); LEDGER_BLE_ACCOUNTS }
        coEvery { noAuthAccountRepository.getAll() } coAnswers { delay(10000L); NO_AUTH_ACCOUNTS }

        val result = sut()

        coroutineDispatcher.scheduler.advanceTimeBy(10001L)

        val expected = ALGO_25_ACCOUNTS + LEDGER_BLE_ACCOUNTS + NO_AUTH_ACCOUNTS
        assertEquals(expected, result)
    }

    companion object {
        private val ALGO_25_ACCOUNTS = peraFixture<List<LocalAccount.Algo25>>()
        private val LEDGER_BLE_ACCOUNTS = peraFixture<List<LocalAccount.LedgerBle>>()
        private val NO_AUTH_ACCOUNTS = peraFixture<List<LocalAccount.NoAuth>>()
    }
}

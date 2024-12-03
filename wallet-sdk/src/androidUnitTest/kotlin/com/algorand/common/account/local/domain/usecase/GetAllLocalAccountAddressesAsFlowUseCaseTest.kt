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

package com.algorand.common.account.local.domain.usecase

import com.algorand.common.account.local.domain.model.LocalAccount.Algo25
import com.algorand.common.account.local.domain.model.LocalAccount.LedgerBle
import com.algorand.common.account.local.domain.model.LocalAccount.NoAuth
import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.common.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.common.testing.peraFixture
import com.algorand.test.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

class GetAllLocalAccountAddressesAsFlowUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mockk()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk()
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk()

    private val sut = GetAllLocalAccountAddressesAsFlowUseCase(
        algo25AccountRepository,
        ledgerBleAccountRepository,
        noAuthAccountRepository
    )

    @Test
    fun `EXPECT empty list when all repositories return empty list`() {
        every { algo25AccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { ledgerBleAccountRepository.getAllAsFlow() } returns flowOf(emptyList())
        every { noAuthAccountRepository.getAllAsFlow() } returns flowOf(emptyList())

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(emptyList())
    }

    @Test
    fun `EXPECT account addresses WHEN there are local accounts`() {
        every { algo25AccountRepository.getAllAsFlow() } returns flowOf(listOf(ALGO_25_ACCOUNT))
        every { ledgerBleAccountRepository.getAllAsFlow() } returns flowOf(listOf(LEDGER_BLE_ACCOUNT))
        every { noAuthAccountRepository.getAllAsFlow() } returns flowOf(listOf(NO_AUTH_ACCOUNT))

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(listOf(ALGO_25_ADDRESS, LEDGER_BLE_ADDRESS, NO_AUTH_ADDRESS))
    }

    companion object {
        private const val ALGO_25_ADDRESS = "address1"
        private val ALGO_25_ACCOUNT = peraFixture<Algo25>().copy(address = ALGO_25_ADDRESS)
        private const val LEDGER_BLE_ADDRESS = "address2"
        private val LEDGER_BLE_ACCOUNT = peraFixture<LedgerBle>().copy(address = LEDGER_BLE_ADDRESS)
        private const val NO_AUTH_ADDRESS = "address4"
        private val NO_AUTH_ACCOUNT = peraFixture<NoAuth>().copy(address = NO_AUTH_ADDRESS)
    }
}

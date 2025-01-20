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

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.test.test
import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.wallet.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.wallet.account.local.domain.repository.NoAuthAccountRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

class GetLocalAccountCountFlowUseCaseTest {
    private val hdKeyAccountRepository: HdKeyAccountRepository = mockk()
    private val algo25AccountRepository: Algo25AccountRepository = mockk()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk()
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk()

    private val sut = GetLocalAccountCountFlowUseCase(
        hdKeyAccountRepository,
        algo25AccountRepository,
        ledgerBleAccountRepository,
        noAuthAccountRepository
    )

    @Test
    fun `EXPECT zero WHEN all repositories return zero`() {
        every { hdKeyAccountRepository.getAccountCountAsFlow() } returns flowOf(0)
        every { algo25AccountRepository.getAccountCountAsFlow() } returns flowOf(0)
        every { ledgerBleAccountRepository.getAccountCountAsFlow() } returns flowOf(0)
        every { noAuthAccountRepository.getAccountCountAsFlow() } returns flowOf(0)

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(0)
    }

    @Test
    fun `EXPECT account count WHEN there are local accounts`() {
        every { hdKeyAccountRepository.getAccountCountAsFlow() } returns flowOf(1)
        every { algo25AccountRepository.getAccountCountAsFlow() } returns flowOf(2)
        every { ledgerBleAccountRepository.getAccountCountAsFlow() } returns flowOf(3)
        every { noAuthAccountRepository.getAccountCountAsFlow() } returns flowOf(4)

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(10)
    }

    @Test
    fun `EXPECT count to be updated WHEN latest count is different than the current one`() {
        every { hdKeyAccountRepository.getAccountCountAsFlow() } returns flowOf(1, 1)
        every { algo25AccountRepository.getAccountCountAsFlow() } returns flowOf(2, 2)
        every { ledgerBleAccountRepository.getAccountCountAsFlow() } returns flowOf(3, 3)
        every { noAuthAccountRepository.getAccountCountAsFlow() } returns flowOf(4, 4)

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValueHistory(10)
    }
}

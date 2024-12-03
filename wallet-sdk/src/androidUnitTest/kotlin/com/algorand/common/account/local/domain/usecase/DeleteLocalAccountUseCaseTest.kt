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

import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.common.account.local.domain.repository.NoAuthAccountRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class DeleteLocalAccountUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mockk(relaxed = true)
    private val noAuthAccountRepository: NoAuthAccountRepository = mockk(relaxed = true)
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mockk(relaxed = true)

    private val deleteLocalAccount = DeleteLocalAccountUseCase(
        algo25AccountRepository = algo25AccountRepository,
        noAuthAccountRepository = noAuthAccountRepository,
        ledgerBleAccountRepository = ledgerBleAccountRepository
    )

    @Test
    fun `EXPECT all account repositories to delete account`() = runTest {
        deleteLocalAccount("address")

        coVerify { algo25AccountRepository.deleteAccount("address") }
        coVerify { ledgerBleAccountRepository.deleteAccount("address") }
        coVerify { noAuthAccountRepository.deleteAccount("address") }
    }
}

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

import com.algorand.common.account.local.domain.usecase.*
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateNoAuthAccountToLedgerBleUseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mockk(relaxed = true)
    private val createLedgerBleAccount: CreateLedgerBleAccount = mockk(relaxed = true)

    private val sut = UpdateNoAuthAccountToLedgerBleUseCase(deleteLocalAccount, createLedgerBleAccount)

    @Test
    fun `EXPECT noAuthAccount to be deleted and new LedgerBleAccount to be created`() = runTest {
        sut(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER)

        coVerify { deleteLocalAccount(ADDRESS) }
        coVerify { createLedgerBleAccount(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER) }
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
        private const val DEVICE_MAC_ADDRESS = "DEVICE_MAC_ADDRESS"
        private const val INDEX_IN_LEDGER = 0
    }
}

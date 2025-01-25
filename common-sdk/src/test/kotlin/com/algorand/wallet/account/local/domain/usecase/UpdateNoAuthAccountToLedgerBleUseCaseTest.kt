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

import com.algorand.wallet.account.local.domain.model.LocalAccount
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateNoAuthAccountToLedgerBleUseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mockk(relaxed = true)
    private val saveLedgerBleAccount: SaveLedgerBleAccount = mockk(relaxed = true)

    private val sut = UpdateNoAuthAccountToLedgerBleUseCase(deleteLocalAccount, saveLedgerBleAccount)

    @Test
    fun `EXPECT noAuthAccount to be deleted and new LedgerBleAccount to be created`() = runTest {
        sut(ADDRESS, DEVICE_MAC_ADDRESS, BLE_ADDRESS, INDEX_IN_LEDGER)

        coVerify { deleteLocalAccount(ADDRESS) }
        coVerify {
            saveLedgerBleAccount(
                LocalAccount.LedgerBle(
                    ADDRESS,
                    DEVICE_MAC_ADDRESS,
                    BLE_ADDRESS,
                    INDEX_IN_LEDGER
                )
            )
        }
    }

    private companion object {
        const val ADDRESS = "ADDRESS"
        const val DEVICE_MAC_ADDRESS = "DEVICE_MAC_ADDRESS"
        const val BLE_ADDRESS = "BLE_ADDRESS"
        const val INDEX_IN_LEDGER = 0
    }
}

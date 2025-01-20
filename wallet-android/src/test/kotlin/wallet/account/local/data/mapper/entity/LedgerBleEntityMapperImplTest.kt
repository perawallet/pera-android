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

package com.algorand.wallet.account.local.data.mapper.entity

import com.algorand.wallet.account.local.data.database.model.LedgerBleEntity
import com.algorand.wallet.account.local.domain.model.LocalAccount
import org.junit.Assert.assertEquals
import org.junit.Test

internal class LedgerBleEntityMapperImplTest {

    private val sut = LedgerBleEntityMapperImpl()

    @Test
    fun `EXPECT mapper entity`() {
        val result = sut(
            LocalAccount.LedgerBle(
                algoAddress = "unencrypted_address",
                deviceMacAddress = "mac_address",
                indexInLedger = 1,
                bluetoothName = "bluetooth_name"
            )
        )

        val expected = LedgerBleEntity(
            algoAddress = "unencrypted_address",
            deviceMacAddress = "mac_address",
            accountIndexInLedger = 1,
            bluetoothName = "bluetooth_name"
        )
        assertEquals(expected, result)
    }
}

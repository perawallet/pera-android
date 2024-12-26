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

package com.algorand.common.account.local.data.mapper.model

import com.algorand.common.account.local.data.database.model.LedgerBleEntity
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.encryption.AddressEncryptionManager
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class LedgerBleMapperImplTest {

    private val encryptionManager: AddressEncryptionManager = mockk()

    private val sut = LedgerBleMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapper model`() {
        coEvery { encryptionManager.decrypt("encrypted_address") } returns "decrypted_address"

        val result = sut(LEDGER_BLE_ENTITY)

        val expected = LocalAccount.LedgerBle(
            address = "decrypted_address",
            deviceMacAddress = LEDGER_BLE_ENTITY.deviceMacAddress,
            indexInLedger = LEDGER_BLE_ENTITY.accountIndexInLedger,
            bluetoothName = LEDGER_BLE_ENTITY.bluetoothName
        )
        assertEquals(expected, result)
    }

    companion object {
        private val LEDGER_BLE_ENTITY = peraFixture<LedgerBleEntity>().copy(encryptedAddress = "encrypted_address")
    }
}

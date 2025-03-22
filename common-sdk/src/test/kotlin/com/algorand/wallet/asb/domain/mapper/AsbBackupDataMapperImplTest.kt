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

package com.algorand.wallet.asb.domain.mapper

import com.algorand.wallet.asb.domain.model.AsbBackupAccount
import com.algorand.wallet.asb.domain.model.AsbBackupData
import com.algorand.wallet.asb.domain.model.BackupProtocolElement
import com.algorand.wallet.asb.domain.model.BackupProtocolPayload
import org.junit.Assert.assertEquals
import org.junit.Test

class AsbBackupDataMapperImplTest {

    private val sut = AsbBackupDataMapperImpl()

    @Test
    fun `EXPECT mapped asb backup payload`() {
        val result = sut(BACKUP_PROTOCOL_PAYLOAD)

        val expected = AsbBackupData(
            deviceId = "device_id",
            providerName = "provider_name",
            accounts = listOf(WATCH_ACCOUNT, STANDARD_ACCOUNT)
        )
        assertEquals(expected, result)
    }

    private companion object {
        val WATCH_ACCOUNT_ELEMENT = BackupProtocolElement(
            address = "watch_address",
            name = "watch_name",
            accountType = "watch",
            privateKey = null,
            metadata = "watch_metadata"
        )

        val WATCH_ACCOUNT = AsbBackupAccount(
            address = "watch_address",
            name = "watch_name",
            metadata = "watch_metadata",
            accountType = AsbBackupAccount.AccountType.Watch
        )

        val STANDARD_ACCOUNT_ELEMENT = BackupProtocolElement(
            address = "standard_address",
            name = "standard_name",
            accountType = "single",
            privateKey = "private_key",
            metadata = "standard_metadata"
        )

        val STANDARD_ACCOUNT = AsbBackupAccount(
            address = "standard_address",
            name = "standard_name",
            metadata = "standard_metadata",
            accountType = AsbBackupAccount.AccountType.Algo25("private_key")
        )

        val UNKNOWN_ACCOUNT_ELEMENT = BackupProtocolElement(
            address = "unknown_address",
            name = "unknown_name",
            accountType = "unknown",
            privateKey = "unknown_pk",
            metadata = "unknown_metadata"
        )

        val BACKUP_PROTOCOL_PAYLOAD = BackupProtocolPayload(
            deviceId = "device_id",
            providerName = "provider_name",
            accounts = listOf(WATCH_ACCOUNT_ELEMENT, STANDARD_ACCOUNT_ELEMENT, UNKNOWN_ACCOUNT_ELEMENT)
        )
    }
}

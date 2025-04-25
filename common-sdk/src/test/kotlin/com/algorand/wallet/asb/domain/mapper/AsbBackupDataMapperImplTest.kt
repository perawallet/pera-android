/*
 * Copyright 2025 Pera Wallet, LDA
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
            accounts = listOf(WATCH_ACCOUNT, ALGO_25_ACCOUNT, ALGO_25_TO_WATCH_ACCOUNT)
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

        val ALGO_25_ACCOUNT_ELEMENT = BackupProtocolElement(
            address = "algo25_address",
            name = "algo25_name",
            accountType = "single",
            privateKey = "private_key",
            metadata = "algo25_metadata"
        )

        val ALGO_25_ACCOUNT = AsbBackupAccount(
            address = "algo25_address",
            name = "algo25_name",
            metadata = "algo25_metadata",
            accountType = AsbBackupAccount.AccountType.Algo25("private_key")
        )

        val ALGO_25_WITHOUT_PRIVATE_KEY_ACCOUNT_ELEMENT = BackupProtocolElement(
            address = "algo25_address",
            name = "algo25_name",
            accountType = "single",
            privateKey = null,
            metadata = "algo25_metadata"
        )

        val ALGO_25_TO_WATCH_ACCOUNT = AsbBackupAccount(
            address = "algo25_address",
            name = "algo25_name",
            metadata = "algo25_metadata",
            accountType = AsbBackupAccount.AccountType.Watch
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
            accounts = listOf(
                WATCH_ACCOUNT_ELEMENT,
                ALGO_25_ACCOUNT_ELEMENT,
                UNKNOWN_ACCOUNT_ELEMENT,
                ALGO_25_WITHOUT_PRIVATE_KEY_ACCOUNT_ELEMENT
            )
        )
    }
}

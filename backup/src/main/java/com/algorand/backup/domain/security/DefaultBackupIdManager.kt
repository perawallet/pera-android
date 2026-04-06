/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.backup.domain.security

import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddress
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultBackupIdManager @Inject constructor(private val algoSdk: AlgoSdkAddress) : BackupIdManager {

    override fun createBackupId(publicKey: SensitiveBytes): PeraResult<BackupId> {
        val address = algoSdk.generateAddressFromPublicKey(publicKey.reveal())
            ?: return PeraResult.Error(IllegalStateException("Failed to derive Algorand address from public key"))
        return PeraResult.Success(BackupId("$BACKUP_ID_PREFIX${address.decodedAddress}"))
    }

    companion object {
        private const val BACKUP_ID_PREFIX = "did:pera:"
    }
}

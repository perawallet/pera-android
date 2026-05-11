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

package com.algorand.android.ui.backup.list.usecase

import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.usecase.FetchAndImportBackupItems
import com.algorand.backup.domain.usecase.GetBackupId
import com.algorand.backup.domain.usecase.ReactivateBackupItem
import com.algorand.wallet.account.local.domain.usecase.GetHdSeedId
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultAddBackupAccountToLocal @Inject constructor(
    private val fetchAndImportBackupItems: FetchAndImportBackupItems,
    private val reactivateBackupItem: ReactivateBackupItem,
    private val getBackupId: GetBackupId,
    private val getHdSeedId: GetHdSeedId
) : AddBackupAccountToLocal {

    override suspend fun invoke(payload: AddressBackupPayload): PeraResult<Unit> {
        val backupId = getBackupId() ?: return PeraResult.Error(IllegalStateException("No backup ID"))
        val keys = buildKeys(payload)
        return when (val importResult = fetchAndImportBackupItems(backupId, keys)) {
            is PeraResult.Error -> importResult
            is PeraResult.Success -> {
                if (payload.address !in importResult.data) {
                    return PeraResult.Error(
                        IllegalStateException("Account ${payload.address} could not be imported from backup")
                    )
                }
                keys.forEach { key -> reactivateBackupItem(backupId, key) }
                PeraResult.Success(Unit)
            }
        }
    }

    private suspend fun buildKeys(payload: AddressBackupPayload): List<BackupItemKey> {
        val keys = mutableListOf(BackupItemKey.accounts(payload.address))
        when (payload) {
            is AddressBackupPayload.Algo25 -> keys += BackupItemKey.secrets(payload.address)
            is AddressBackupPayload.HdSeed -> keys += BackupItemKey.secrets(payload.address)
            is AddressBackupPayload.HdKey -> {
                if (getHdSeedId(payload.seedFirstDerivedAddress) == null) {
                    keys += BackupItemKey.secrets(payload.seedFirstDerivedAddress)
                }
            }
            is AddressBackupPayload.LedgerBle,
            is AddressBackupPayload.NoAuth,
            is AddressBackupPayload.Joint -> Unit
        }
        return keys
    }
}

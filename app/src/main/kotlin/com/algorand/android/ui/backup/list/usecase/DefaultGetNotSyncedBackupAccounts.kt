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

import com.algorand.android.ui.backup.list.mapper.BackupAccountIconPreviewMapper
import com.algorand.android.ui.backup.list.model.BackupAccountListItem
import com.algorand.backup.domain.usecase.GetAddressBackupSnapshot
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import javax.inject.Inject

internal class DefaultGetNotSyncedBackupAccounts @Inject constructor(
    private val getAddressBackupSnapshot: GetAddressBackupSnapshot,
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    private val backupAccountIconPreviewMapper: BackupAccountIconPreviewMapper
) : GetNotSyncedBackupAccounts {

    override suspend fun invoke(): List<BackupAccountListItem> {
        val localAddresses = getLocalAccountsAddresses().toHashSet()
        return getAddressBackupSnapshot()
            .filter { it.address !in localAddresses }
            .map { payload ->
                BackupAccountListItem(
                    displayName = payload.customName ?: payload.address,
                    address = payload.address,
                    iconPreview = backupAccountIconPreviewMapper.mapFromAddressBackupPayload(payload),
                    payload = payload
                )
            }
    }
}

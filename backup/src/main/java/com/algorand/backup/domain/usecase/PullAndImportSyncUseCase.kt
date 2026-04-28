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

package com.algorand.backup.domain.usecase

import com.algorand.backup.domain.model.PullSyncResult
import com.algorand.backup.domain.model.SyncBackupResult
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class PullAndImportSyncUseCase @Inject constructor(
    private val getBackupId: GetBackupId,
    private val pullBackupSync: PullBackupSync,
    private val fetchAndImportBackupItems: FetchAndImportBackupItems
) : PullAndImportSync {

    override suspend fun invoke(): SyncBackupResult {
        val backupId = getBackupId() ?: return SyncBackupResult.Error(IllegalStateException("No backup ID"))
        return when (val result = pullBackupSync(backupId)) {
            is PullSyncResult.UpToDate -> SyncBackupResult.Success
            is PullSyncResult.Error -> SyncBackupResult.Error(result.exception)
            is PullSyncResult.Updated -> {
                when (val importResult = fetchAndImportBackupItems(backupId, result.updatedKeys)) {
                    is PeraResult.Success -> SyncBackupResult.Success
                    is PeraResult.Error -> SyncBackupResult.Error(importResult.exception)
                }
            }
        }
    }
}

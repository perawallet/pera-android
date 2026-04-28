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

import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DeleteBackupUseCase @Inject constructor(
    private val getBackupId: GetBackupId,
    private val backupRepository: BackupRepository,
    private val disableBackup: DisableBackup
) : DeleteBackup {

    override suspend fun invoke(): PeraResult<Unit> {
        val backupId = getBackupId() ?: run {
            disableBackup()
            return PeraResult.Success(Unit)
        }
        val result = backupRepository.deleteBackup(backupId).map { }
        disableBackup()
        return result
    }
}

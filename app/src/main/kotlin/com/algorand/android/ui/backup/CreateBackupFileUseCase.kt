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

package com.algorand.android.ui.backup

import com.algorand.android.ui.backup.mapper.BackupFileMapper
import com.algorand.android.ui.backup.model.BackupFile
import com.algorand.backup.domain.usecase.GenerateEncodedArgon2idHash
import com.algorand.backup.domain.usecase.RevealBackupAuthCredentials
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.utils.date.TimeProvider
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CreateBackupFileUseCase @Inject constructor(
    private val revealBackupAuthCredentials: RevealBackupAuthCredentials,
    private val generateEncodedArgon2idHash: GenerateEncodedArgon2idHash,
    private val backupFileMapper: BackupFileMapper,
    private val timeProvider: TimeProvider
) : CreateBackupFile {

    override operator fun invoke(): PeraResult<String> {
        val address = when (val result = revealBackupAuthCredentials { backupId, _, _ -> backupId.address }) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> return PeraResult.Error(result.exception)
        }
        val encodedHash = when (val result = generateEncodedArgon2idHash()) {
            is PeraResult.Success -> result.data
            is PeraResult.Error -> return PeraResult.Error(result.exception)
        }

        val date = timeProvider.getZonedDateTimeNow().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val backupFile = BackupFile(address = address, encodedHash = encodedHash, date = date)
        return PeraResult.Success(backupFileMapper.toJson(backupFile))
    }
}

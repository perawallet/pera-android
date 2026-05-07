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

import android.content.Context
import android.net.Uri
import com.algorand.android.ui.backup.mapper.BackupFileMapper
import com.algorand.android.ui.backup.model.BackupFile
import com.algorand.android.ui.backup.model.BackupFileImportResult
import com.algorand.backup.domain.security.Argon2idEncoder
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

class DefaultBackupFileImporter @Inject constructor(
    private val backupFileMapper: BackupFileMapper,
    private val argon2idEncoder: Argon2idEncoder
) : BackupFileImporter {

    override fun importFile(context: Context, uri: Uri): BackupFileImportResult {
        val content = readFileContent(context, uri) ?: return BackupFileImportResult.Error.FileUnreadable
        val backupFile = parseBackupFile(content) ?: return BackupFileImportResult.Error.InvalidBackupFile
        if (!isHashValid(backupFile)) return BackupFileImportResult.Error.InvalidBackupFile
        return BackupFileImportResult.Success(backupFile)
    }

    private fun readFileContent(context: Context, uri: Uri): String? {
        val size = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: return null
        if (size > MAX_FILE_SIZE_BYTES) return null
        return context.contentResolver.openInputStream(uri)?.use {
            it.bufferedReader().readText()
        }?.trim()?.takeIf { it.isNotBlank() }
    }

    private fun parseBackupFile(content: String): BackupFile? {
        return backupFileMapper.fromJson(content)
    }

    private fun isHashValid(backupFile: BackupFile): Boolean {
        return argon2idEncoder.decode(backupFile.encodedHash) is PeraResult.Success
    }

    private companion object {
        const val MAX_FILE_SIZE_BYTES = 50 * 1024L
    }
}

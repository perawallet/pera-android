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

package com.algorand.android.ui.backup.mapper

import com.algorand.android.ui.backup.model.BackupFile
import com.google.gson.Gson
import javax.inject.Inject

class DefaultBackupFileMapper @Inject constructor(private val gson: Gson) : BackupFileMapper {

    override fun toJson(backupFile: BackupFile): String {
        return gson.toJson(backupFile)
    }

    override fun fromJson(json: String): BackupFile? {
        return try {
            val backupFile = gson.fromJson(json, BackupFile::class.java)
            backupFile.takeIf { it.isValid() }
        } catch (_: Exception) {
            null
        }
    }

    private fun BackupFile?.isValid(): Boolean {
        return this != null && address.isNotBlank() && encodedHash.isNotBlank() && date.isNotBlank()
    }
}

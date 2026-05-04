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

package com.algorand.android.ui.backup.sync.security

import com.algorand.backup.domain.model.Argon2idConfig

data class BackupSyncPayload(
    val mnemonic: String,
    val salt: ByteArray,
    val argon2idConfig: Argon2idConfig
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BackupSyncPayload) return false
        return mnemonic == other.mnemonic &&
            salt.contentEquals(other.salt) &&
            argon2idConfig == other.argon2idConfig
    }

    override fun hashCode(): Int {
        var result = mnemonic.hashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + argon2idConfig.hashCode()
        return result
    }
}

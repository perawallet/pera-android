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

package com.algorand.backup.data.model

import com.google.gson.annotations.SerializedName

internal data class BackupAuthCredentialsCacheData(
    @SerializedName("backup_id") val backupId: String,
    @SerializedName("wrapped_mnemonic") val wrappedMnemonic: String,
    @SerializedName("mnemonic_iv") val mnemonicIv: String,
    @SerializedName("wrapped_salt") val wrappedSalt: String,
    @SerializedName("salt_iv") val saltIv: String
)

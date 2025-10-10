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

package com.algorand.android.credentials.passkeys.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passkey_table")
internal data class PasskeyEntity(
    @PrimaryKey
    @ColumnInfo(name = "credential_id")
    val credentialId: String,
    @ColumnInfo(name = "site_id")
    val siteId: Long,
    @ColumnInfo(name = "bip39_address")
    val bip39Address: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "user_display_name")
    val userDisplayName: String?,
    @ColumnInfo(name = "last_used_time_ms")
    val lastUsedTimeMs: Long?
)

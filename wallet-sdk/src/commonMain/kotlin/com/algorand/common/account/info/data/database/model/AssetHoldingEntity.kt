/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.info.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "asset_holding_table",
    indices = [Index(value = ["encrypted_address", "asset_id"], unique = true)]
)
internal data class AssetHoldingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "asset_id")
    val assetId: Long,

    @ColumnInfo(name = "amount")
    val amount: String,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "is_frozen")
    val isFrozen: Boolean,

    @ColumnInfo(name = "opted_in_at_round")
    val optedInAtRound: Long?,

    @ColumnInfo(name = "opted_out_at_round")
    val optedOutAtRound: Long?,

    @ColumnInfo(name = "asset_status")
    val assetStatusEntity: AssetStatusEntity
)

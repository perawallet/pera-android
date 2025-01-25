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

package com.algorand.wallet.account.local.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hd_keys",
    indices = [
        Index(value = ["public_key"], unique = true)
    ]
)
internal data class HdKeyEntity(
    @PrimaryKey
    @ColumnInfo("algo_address")
    val algoAddress: String,

    @ColumnInfo("public_key", typeAffinity = ColumnInfo.BLOB)
    val publicKey: ByteArray,

    @ColumnInfo("encrypted_private_key", typeAffinity = ColumnInfo.BLOB)
    val encryptedPrivateKey: ByteArray,

    @ColumnInfo("seed_id")
    val seedId: Int,

    @ColumnInfo("account")
    val account: Int,

    @ColumnInfo("change")
    val change: Int,

    @ColumnInfo("key_index")
    val keyIndex: Int,

    @ColumnInfo("derivation_type")
    val derivationType: Int
)

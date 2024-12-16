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
import androidx.room.PrimaryKey

@Entity(tableName = "account_information")
internal data class AccountInformationEntity(
    @PrimaryKey
    @ColumnInfo(name = "encrypted_address")
    val encryptedAddress: String,

    @ColumnInfo(name = "algo_amount")
    val algoAmount: String,

    @ColumnInfo(name = "opted_in_apps_count")
    val optedInAppsCount: Int,

    @ColumnInfo(name = "apps_total_extra_pages")
    val appsTotalExtraPages: Int,

    @ColumnInfo(name = "auth_address")
    val authAddress: String?,

    @ColumnInfo(name = "created_at_round")
    val createdAtRound: Long?,

    @ColumnInfo(name = "last_fetched_round")
    val lastFetchedRound: Long,

    @ColumnInfo(name = "total_created_apps_count")
    val totalCreatedAppsCount: Int,

    @ColumnInfo(name = "total_created_assets_count")
    val totalCreatedAssetsCount: Int,

    @ColumnInfo(name = "app_state_schema_num_byte_slice")
    val appStateNumByteSlice: Long?,

    @ColumnInfo(name = "app_state_schema_num_uint")
    val appStateSchemaUint: Long?
)

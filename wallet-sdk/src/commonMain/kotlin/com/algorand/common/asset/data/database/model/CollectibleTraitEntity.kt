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

package com.algorand.common.asset.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.algorand.common.asset.data.database.model.CollectibleTraitEntity.Companion.COLLECTIBLE_TRAIT_ENTITY_NAME

@Entity(tableName = COLLECTIBLE_TRAIT_ENTITY_NAME)
internal data class CollectibleTraitEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0L,

    @ColumnInfo("collectible_asset_id")
    val collectibleAssetId: Long,

    @ColumnInfo("display_name")
    val displayName: String?,

    @ColumnInfo("display_value")
    val displayValue: String?
) {
    internal companion object {
        const val COLLECTIBLE_TRAIT_ENTITY_NAME = "collectible_trait"
    }
}

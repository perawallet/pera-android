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
import com.algorand.common.asset.data.database.model.AssetDetailEntity.Companion.ASSET_DETAIL_TABLE_NAME

@Entity(tableName = ASSET_DETAIL_TABLE_NAME)
internal data class AssetDetailEntity(
    @PrimaryKey
    @ColumnInfo("asset_id")
    val assetId: Long,

    @ColumnInfo("name")
    val name: String?,

    @ColumnInfo("unit_name")
    val unitName: String?,

    @ColumnInfo("decimals")
    val decimals: Int,

    @ColumnInfo("usd_value")
    val usdValue: String?,

    @ColumnInfo("max_supply")
    val maxSupply: String,

    @ColumnInfo("explorer_url")
    val explorerUrl: String?,

    @ColumnInfo("project_url")
    val projectUrl: String?,

    @ColumnInfo("project_name")
    val projectName: String?,

    @ColumnInfo("logo_svg_url")
    val logoSvgUrl: String?,

    @ColumnInfo("logo_url")
    val logoUrl: String?,

    @ColumnInfo("discord_url")
    val discordUrl: String?,

    @ColumnInfo("telegram_url")
    val telegramUrl: String?,

    @ColumnInfo("twitter_username")
    val twitterUsername: String?,

    @ColumnInfo("description")
    val description: String?,

    @ColumnInfo("url")
    val url: String?,

    @ColumnInfo("total_supply")
    val totalSupply: String?,

    @ColumnInfo("last_24_hours_algo_price_change_percentage")
    val last24HoursAlgoPriceChangePercentage: String?,

    @ColumnInfo("available_on_discover_mobile")
    val availableOnDiscoverMobile: Boolean,

    @ColumnInfo("asset_creator_id")
    val assetCreatorId: Long?,

    @ColumnInfo("asset_creator_address")
    val assetCreatorAddress: String?,

    @ColumnInfo("is_verified_asset_creator")
    val isVerifiedAssetCreator: Boolean?,

    @ColumnInfo("verification_tier")
    val verificationTier: VerificationTierEntity
) {

    internal companion object {
        const val ASSET_DETAIL_TABLE_NAME = "asset_detail"
    }
}

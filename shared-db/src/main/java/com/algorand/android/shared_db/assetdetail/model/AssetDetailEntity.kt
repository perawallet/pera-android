package com.algorand.android.shared_db.assetdetail.model

import androidx.room.*
import com.algorand.android.shared_db.assetdetail.model.AssetDetailEntity.Companion.ASSET_DETAIL_TABLE_NAME
import java.math.*

@Entity(tableName = ASSET_DETAIL_TABLE_NAME)
data class AssetDetailEntity(
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
    val usdValue: BigDecimal?,

    @ColumnInfo("max_supply")
    val maxSupply: BigInteger,

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
    val totalSupply: BigDecimal?,

    @ColumnInfo("last_24_hours_algo_price_change_percentage")
    val last24HoursAlgoPriceChangePercentage: BigDecimal?,

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

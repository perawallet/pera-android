package com.algorand.wallet.asset.data.database.model

import androidx.room.ColumnInfo
import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity
import java.math.BigDecimal
import java.math.BigInteger

internal data class PaginatedAssetCollectibleItemDto(
    @ColumnInfo(name = "algo_address")
    val address: String,

    @ColumnInfo(name = "amount")
    val amount: BigInteger,

    @ColumnInfo(name = "opted_in_at_round")
    val optedInAtRound: Long?,

    @ColumnInfo(name = "asset_status")
    val assetStatusEntity: AssetStatusEntity,

    @ColumnInfo(name = "asset_id")
    val assetId: Long,

    @ColumnInfo("usd_value")
    val usdValue: BigDecimal?,

    @ColumnInfo("decimals")
    val decimals: Int,

    @ColumnInfo("total_usd_value")
    val totalUsdValue: BigDecimal?,

    @ColumnInfo("name")
    val assetName: String?,

    @ColumnInfo("unit_name")
    val assetShortName: String?,

    @ColumnInfo("logo_url")
    val logoUrl: String?,

    @ColumnInfo("title")
    val collectibleName: String?,

    @ColumnInfo("primary_image_url")
    val collectibleImageUrl: String?,

    @ColumnInfo("collection_name")
    val collectionName: String?,

    @ColumnInfo("verification_tier")
    val verificationTierEntity: VerificationTierEntity,

    @ColumnInfo("media_type")
    val mediaTypeEntity: CollectibleMediaTypeEntity?
)

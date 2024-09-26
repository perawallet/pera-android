package com.algorand.android.module.asset.detail.component.collectible.domain.model

import com.algorand.android.module.asset.detail.component.asset.domain.model.*
import java.math.*

internal data class CollectibleDetail(
    val collectibleAssetId: Long,
    val fullName: String?,
    val shortName: String?,
    val fractionDecimals: Int,
    val usdValue: BigDecimal?,
    val assetCreator: AssetCreator?,
    val mediaType: CollectibleMediaType,
    val primaryImageUrl: String?,
    val title: String?,
    val collectionName: String?,
    val description: String?,
    val traits: List<CollectibleTrait>,
    val medias: List<BaseCollectibleMedia>,
    val explorerUrl: String?,
    val totalSupply: BigDecimal?,
    val verificationTier: VerificationTier,
    val logoUri: String?,
    val logoSvgUri: String?,
    val projectUrl: String?,
    val projectName: String?,
    val discordUrl: String?,
    val telegramUrl: String?,
    val twitterUsername: String?,
    val assetDescription: String?,
    val url: String?,
    val maxSupply: BigInteger?,
    val last24HoursAlgoPriceChangePercentage: BigDecimal?,
    val isAvailableOnDiscoverMobile: Boolean?
)

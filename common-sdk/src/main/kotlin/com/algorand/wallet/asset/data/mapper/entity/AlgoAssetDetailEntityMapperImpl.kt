package com.algorand.wallet.asset.data.mapper.entity

import com.algorand.wallet.asset.data.database.model.AssetDetailEntity
import com.algorand.wallet.asset.data.database.model.VerificationTierEntity
import com.algorand.wallet.asset.domain.util.AssetConstants
import java.math.BigDecimal
import javax.inject.Inject

internal class AlgoAssetDetailEntityMapperImpl @Inject constructor() : AlgoAssetDetailEntityMapper {

    override fun invoke(usdValue: BigDecimal?): AssetDetailEntity {
        return AssetDetailEntity(
            assetId = AssetConstants.ALGO_ID,
            name = AssetConstants.ALGO_FULL_NAME,
            unitName = AssetConstants.ALGO_SHORT_NAME,
            decimals = AssetConstants.ALGO_DECIMALS,
            usdValue = usdValue,
            maxSupply = "",
            totalSupply = AssetConstants.ALGO_TOTAL_SUPPLY.toPlainString(),
            explorerUrl = null,
            projectUrl = null,
            projectName = null,
            discordUrl = AssetConstants.ALGORAND_DISCORD_URL,
            telegramUrl = AssetConstants.ALGORAND_TELEGRAM_URL,
            twitterUsername = AssetConstants.ALGORAND_TWITTER_USERNAME,
            logoUrl = null,
            logoSvgUrl = null,
            description = null,
            url = AssetConstants.ALGORAND_WEBSITE_URL,
            last24HoursAlgoPriceChangePercentage = null,
            availableOnDiscoverMobile = true,
            assetCreatorId = null,
            assetCreatorAddress = null,
            isVerifiedAssetCreator = true,
            verificationTier = VerificationTierEntity.TRUSTED
        )
    }
}

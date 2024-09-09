package com.algorand.android.assetdetail.component.asset.data.mapper.entity

import com.algorand.android.assetdetail.component.asset.data.model.AssetResponse
import com.algorand.android.shared_db.assetdetail.model.AssetDetailEntity
import java.math.*
import javax.inject.Inject

internal class AssetDetailEntityMapperImpl @Inject constructor(
    private val verificationTierEntityMapper: VerificationTierEntityMapper
) : AssetDetailEntityMapper {

    override fun invoke(response: AssetResponse): AssetDetailEntity? {
        return with(response) {
            AssetDetailEntity(
                assetId = assetId ?: return null,
                name = fullName,
                unitName = shortName,
                decimals = fractionDecimals ?: 0,
                usdValue = usdValue,
                maxSupply = maxSupply ?: BigInteger.ZERO,
                explorerUrl = explorerUrl,
                projectUrl = projectUrl,
                projectName = projectName,
                logoSvgUrl = logoSvgUri,
                logoUrl = logoUri,
                description = description,
                totalSupply = totalSupply ?: BigDecimal.ZERO,
                url = url,
                telegramUrl = telegramUrl,
                twitterUsername = twitterUsername,
                discordUrl = discordUrl,
                availableOnDiscoverMobile = isAvailableOnDiscoverMobile ?: false,
                last24HoursAlgoPriceChangePercentage = last24HoursAlgoPriceChangePercentage,
                verificationTier = verificationTierEntityMapper(verificationTier),
                assetCreatorAddress = assetCreator?.publicKey,
                assetCreatorId = assetCreator?.id,
                isVerifiedAssetCreator = assetCreator?.isVerifiedAssetCreator
            )
        }
    }
}

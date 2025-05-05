package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.account.info.data.mapper.model.AssetStatusMapper
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import com.algorand.wallet.asset.domain.model.AssetLite
import javax.inject.Inject

internal class AssetLiteMapperImpl @Inject constructor(
    private val verificationTierMapper: VerificationTierMapper,
    private val assetStatusMapper: AssetStatusMapper
) : AssetLiteMapper {

    override fun invoke(dto: PaginatedAssetCollectibleItemDto): AssetLite {
        return with(dto) {
            AssetLite(
                address = address,
                amount = amount,
                usdValue = usdValue,
                decimal = decimals,
                assetId = assetId,
                name = assetName,
                shortName = assetShortName,
                verificationTier = verificationTierMapper(verificationTierEntity),
                totalUsdValue = totalUsdValue,
                assetStatus = assetStatusMapper(assetStatusEntity),
                type = if (collectibleName != null) {
                    AssetLite.Type.Collectible(
                        name = collectibleName,
                        logoUrl = collectibleImageUrl,
                        collectionName = collectionName
                    )
                } else {
                    AssetLite.Type.Asset(logoUrl)
                }
            )
        }
    }
}

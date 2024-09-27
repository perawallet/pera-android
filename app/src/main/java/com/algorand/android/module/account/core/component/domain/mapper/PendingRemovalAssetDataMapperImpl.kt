package com.algorand.android.module.account.core.component.domain.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.Asset
import javax.inject.Inject

internal class PendingRemovalAssetDataMapperImpl @Inject constructor() : PendingRemovalAssetDataMapper {

    override fun invoke(asset: Asset): BaseAccountAssetData.PendingAssetData.DeletionAssetData {
        return BaseAccountAssetData.PendingAssetData.DeletionAssetData(
            id = asset.id,
            name = asset.assetInfo?.name?.fullName,
            shortName = asset.assetInfo?.name?.shortName,
            isAlgo = false,
            decimals = asset.getDecimalsOrZero(),
            creatorPublicKey = asset.assetInfo?.creator?.publicKey,
            usdValue = asset.assetInfo?.fiat?.usdValue,
            verificationTier = asset.verificationTier
        )
    }
}

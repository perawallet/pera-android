package com.algorand.android.core.component.collectible.domain.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.detail.ImageCollectibleDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleImageData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger

internal interface OwnedCollectibleImageDataMapper {

    operator fun invoke(
        collectibleDetail: ImageCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleImageData
}

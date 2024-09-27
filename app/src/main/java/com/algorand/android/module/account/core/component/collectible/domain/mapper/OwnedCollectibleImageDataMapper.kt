package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleImageData
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.ImageCollectibleDetail
import com.algorand.android.module.parity.domain.model.ParityValue
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

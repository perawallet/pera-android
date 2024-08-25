package com.algorand.android.core.component.collectible.domain.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.detail.MixedCollectibleDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleMixedData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger

internal interface OwnedCollectibleMixedDataMapper {

    operator fun invoke(
        collectibleDetail: MixedCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleMixedData
}

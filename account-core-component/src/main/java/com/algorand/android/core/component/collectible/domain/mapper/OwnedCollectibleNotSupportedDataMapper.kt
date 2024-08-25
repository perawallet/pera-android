package com.algorand.android.core.component.collectible.domain.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.detail.UnsupportedCollectibleDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleUnsupportedData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger

internal interface OwnedCollectibleNotSupportedDataMapper {

    operator fun invoke(
        collectibleDetail: UnsupportedCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleUnsupportedData
}

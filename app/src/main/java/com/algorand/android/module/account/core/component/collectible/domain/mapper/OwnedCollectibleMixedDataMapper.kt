package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleMixedData
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.MixedCollectibleDetail
import com.algorand.android.module.parity.domain.model.ParityValue
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

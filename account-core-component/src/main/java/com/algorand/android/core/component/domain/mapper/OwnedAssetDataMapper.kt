package com.algorand.android.core.component.domain.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger

internal interface OwnedAssetDataMapper {
    operator fun invoke(
        assetDetail: AssetDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
}

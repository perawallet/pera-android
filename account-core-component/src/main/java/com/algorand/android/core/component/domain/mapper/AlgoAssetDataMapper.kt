package com.algorand.android.core.component.domain.mapper

import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.*

internal interface AlgoAssetDataMapper {
    operator fun invoke(
        amount: BigInteger,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        usdValue: BigDecimal
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
}

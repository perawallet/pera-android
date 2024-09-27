package com.algorand.android.module.account.core.component.domain.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.BigDecimal
import java.math.BigInteger

internal interface AlgoAssetDataMapper {
    operator fun invoke(
        amount: BigInteger,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        usdValue: BigDecimal
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
}

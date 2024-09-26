package com.algorand.android.module.parity.domain.usecase.secondary

import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.*

interface GetSecondaryCurrencyAssetParityValue {
    operator fun invoke(usdValue: BigDecimal, decimals: Int, amount: BigInteger): ParityValue
}

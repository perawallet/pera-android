package com.algorand.android.parity.domain.usecase.secondary

import com.algorand.android.parity.domain.model.ParityValue
import java.math.*

interface GetSecondaryCurrencyAssetParityValue {
    operator fun invoke(usdValue: BigDecimal, decimals: Int, amount: BigInteger): ParityValue
}

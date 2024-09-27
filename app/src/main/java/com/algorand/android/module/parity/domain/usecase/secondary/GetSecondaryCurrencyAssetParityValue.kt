package com.algorand.android.module.parity.domain.usecase.secondary

import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.BigDecimal
import java.math.BigInteger

interface GetSecondaryCurrencyAssetParityValue {
    operator fun invoke(usdValue: BigDecimal, decimals: Int, amount: BigInteger): ParityValue
}

package com.algorand.android.parity.domain.usecase.primary

import com.algorand.android.parity.domain.model.ParityValue
import java.math.*

interface GetPrimaryCurrencyAssetParityValue {
    operator fun invoke(usdValue: BigDecimal, decimals: Int, amount: BigInteger): ParityValue
}

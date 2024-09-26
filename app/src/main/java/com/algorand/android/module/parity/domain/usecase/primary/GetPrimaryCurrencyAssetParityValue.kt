package com.algorand.android.module.parity.domain.usecase.primary

import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.*

interface GetPrimaryCurrencyAssetParityValue {
    operator fun invoke(usdValue: BigDecimal, decimals: Int, amount: BigInteger): ParityValue
}

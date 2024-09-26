package com.algorand.android.module.parity.domain.usecase.primary.implementation

import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.CalculateParityValue
import com.algorand.android.module.parity.domain.usecase.primary.*
import java.math.*
import javax.inject.Inject

internal class GetPrimaryCurrencyAssetParityValueUseCase @Inject constructor(
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val calculateParityValue: CalculateParityValue
) : GetPrimaryCurrencyAssetParityValue {

    override fun invoke(usdValue: BigDecimal, decimals: Int, amount: BigInteger): ParityValue {
        return calculateParityValue(
            usdValue,
            decimals,
            amount,
            getUsdToPrimaryCurrencyConversionRate(),
            getPrimaryCurrencySymbol().orEmpty()
        )
    }
}

package com.algorand.android.module.parity.domain.usecase.secondary.implementation

import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.CalculateParityValue
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.secondary.GetUsdToSecondaryCurrencyConversionRate
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class GetSecondaryCurrencyAssetParityValueUseCase @Inject constructor(
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val calculateParityValue: CalculateParityValue
) : GetSecondaryCurrencyAssetParityValue {

    override fun invoke(usdValue: BigDecimal, decimals: Int, amount: BigInteger): ParityValue {
        val usdToSelectedCurrencyConversionRate = getUsdToSecondaryCurrencyConversionRate()
        val selectedCurrencySymbol = getSecondaryCurrencySymbol().orEmpty()
        return calculateParityValue(
            usdValue,
            decimals,
            amount,
            usdToSelectedCurrencyConversionRate,
            selectedCurrencySymbol
        )
    }
}

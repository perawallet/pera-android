package com.algorand.android.module.parity.domain.usecase.secondary.implementation

import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.CalculateParityValue
import com.algorand.android.module.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryAlgoParityValue
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.secondary.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.module.parity.domain.util.ParityConstants.ALGO_DECIMALS
import java.math.BigInteger
import javax.inject.Inject

internal class GetSecondaryAlgoParityValueUseCase @Inject constructor(
    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate,
    private val getUsdToSecondaryCurrencyConversionRate: GetUsdToSecondaryCurrencyConversionRate,
    private val getSecondaryCurrencySymbol: GetSecondaryCurrencySymbol,
    private val calculateParityValue: CalculateParityValue
) : GetSecondaryAlgoParityValue {

    override fun invoke(algoAmount: BigInteger): ParityValue {
        return calculateParityValue(
            assetUsdValue = getAlgoToUsdConversionRate(),
            assetDecimals = ALGO_DECIMALS,
            amount = algoAmount,
            conversionRate = getUsdToSecondaryCurrencyConversionRate(),
            currencySymbol = getSecondaryCurrencySymbol().orEmpty()
        )
    }
}

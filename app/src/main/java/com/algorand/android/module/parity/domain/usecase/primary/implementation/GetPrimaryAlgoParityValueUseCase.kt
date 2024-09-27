package com.algorand.android.module.parity.domain.usecase.primary.implementation

import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.CalculateParityValue
import com.algorand.android.module.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryAlgoParityValue
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencySymbol
import com.algorand.android.module.parity.domain.usecase.primary.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.module.parity.domain.util.ParityConstants.ALGO_DECIMALS
import java.math.BigInteger
import javax.inject.Inject

internal class GetPrimaryAlgoParityValueUseCase @Inject constructor(
    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate,
    private val getUsdToPrimaryCurrencyConversionRate: GetUsdToPrimaryCurrencyConversionRate,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val calculateParityValue: CalculateParityValue
) : GetPrimaryAlgoParityValue {

    override fun invoke(algoAmount: BigInteger): ParityValue {
        return calculateParityValue(
            assetUsdValue = getAlgoToUsdConversionRate(),
            assetDecimals = ALGO_DECIMALS,
            amount = algoAmount,
            conversionRate = getUsdToPrimaryCurrencyConversionRate(),
            currencySymbol = getPrimaryCurrencySymbol().orEmpty()
        )
    }
}

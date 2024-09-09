package com.algorand.android.parity.domain.usecase.implementation

import com.algorand.android.parity.domain.model.AlgoAmountValue
import com.algorand.android.parity.domain.usecase.*
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryAlgoParityValue
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryAlgoParityValue
import java.math.BigInteger
import javax.inject.Inject

internal class GetAlgoAmountValueUseCase @Inject constructor(
    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate,
    private val getPrimaryAlgoParityValue: GetPrimaryAlgoParityValue,
    private val getSecondaryAlgoParityValue: GetSecondaryAlgoParityValue
) : GetAlgoAmountValue {

    override fun invoke(algoAmount: BigInteger): AlgoAmountValue {
        return AlgoAmountValue(
            amount = algoAmount,
            parityValueInSelectedCurrency = getPrimaryAlgoParityValue(algoAmount),
            parityValueInSecondaryCurrency = getSecondaryAlgoParityValue(algoAmount),
            usdValue = getAlgoToUsdConversionRate()
        )
    }
}

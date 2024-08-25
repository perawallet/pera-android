package com.algorand.android.parity.domain.usecase

import com.algorand.android.parity.domain.model.AlgoAmountValue
import java.math.BigInteger

interface GetAlgoAmountValue {
    operator fun invoke(algoAmount: BigInteger): AlgoAmountValue
}

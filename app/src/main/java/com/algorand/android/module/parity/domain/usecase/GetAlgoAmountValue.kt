package com.algorand.android.module.parity.domain.usecase

import com.algorand.android.module.parity.domain.model.AlgoAmountValue
import java.math.BigInteger

interface GetAlgoAmountValue {
    operator fun invoke(algoAmount: BigInteger): AlgoAmountValue
}

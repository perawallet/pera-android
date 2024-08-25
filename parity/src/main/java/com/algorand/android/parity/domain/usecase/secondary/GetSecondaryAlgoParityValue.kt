package com.algorand.android.parity.domain.usecase.secondary

import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger

interface GetSecondaryAlgoParityValue {
    operator fun invoke(algoAmount: BigInteger): ParityValue
}

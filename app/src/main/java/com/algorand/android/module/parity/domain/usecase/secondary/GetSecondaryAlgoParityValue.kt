package com.algorand.android.module.parity.domain.usecase.secondary

import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.BigInteger

interface GetSecondaryAlgoParityValue {
    operator fun invoke(algoAmount: BigInteger): ParityValue
}

package com.algorand.android.module.parity.domain.usecase.primary

import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.BigInteger

interface GetPrimaryAlgoParityValue {
    operator fun invoke(algoAmount: BigInteger): ParityValue
}

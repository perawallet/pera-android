/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.parity.domain.usecase

import com.algorand.android.modules.parity.domain.repository.ParityRepository
import com.algorand.android.modules.parity.utils.ParityConstants.SAFE_PARITY_DIVISION_DECIMALS
import com.algorand.android.utils.CacheResult
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

internal class GetUsdToAlgoConversionRateUseCase @Inject constructor(
    private val parityRepository: ParityRepository
) : GetUsdToAlgoConversionRate {

    override fun invoke(): BigDecimal {
        return (parityRepository.getCachedSelectedCurrencyDetail() as? CacheResult.Success?)?.data?.run {
            if (algoToSelectedCurrencyConversionRate == BigDecimal.ZERO ||
                algoToSelectedCurrencyConversionRate == null
            ) {
                BigDecimal.ZERO
            } else {
                usdToSelectedCurrencyConversionRate?.divide(
                    algoToSelectedCurrencyConversionRate,
                    SAFE_PARITY_DIVISION_DECIMALS,
                    RoundingMode.UP
                )
            }
        } ?: BigDecimal.ZERO
    }
}

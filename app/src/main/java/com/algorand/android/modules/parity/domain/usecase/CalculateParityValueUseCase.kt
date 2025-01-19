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

import com.algorand.android.modules.parity.domain.model.ParityValue
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class CalculateParityValueUseCase @Inject constructor() : CalculateParityValue {

    override fun invoke(
        assetUsdValue: BigDecimal,
        assetDecimals: Int,
        amount: BigInteger,
        conversionRate: BigDecimal,
        currencySymbol: String
    ): ParityValue {
        val amountInSelectedCurrency = amount.toBigDecimal().movePointLeft(assetDecimals)
            .multiply(conversionRate)
            .multiply(assetUsdValue)
        return ParityValue(amountInSelectedCurrency, currencySymbol)
    }
}

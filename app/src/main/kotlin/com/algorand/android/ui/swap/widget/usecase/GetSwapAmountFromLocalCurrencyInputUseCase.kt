/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.swap.widget.usecase

import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryFiatConversionRate
import com.algorand.android.utils.orZero
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

class GetSwapAmountFromLocalCurrencyInputUseCase @Inject constructor(
    private val getUsdToPrimaryFiatConversionRate: GetUsdToPrimaryFiatConversionRate
) : GetSwapAmountFromLocalCurrencyInput {

    override fun invoke(amountInput: BigDecimal, assetInDetail: SwapSelectedAssetDetail): BigInteger {
        val assetUsdValue = assetInDetail.usdValue.orZero()
        val usdToLocalCurrencyRate = getUsdToPrimaryFiatConversionRate()
        if (assetUsdValue == BigDecimal.ZERO || usdToLocalCurrencyRate == BigDecimal.ZERO) return BigInteger.ZERO
        val localCurrencyValue = assetUsdValue.multiply(usdToLocalCurrencyRate)
        return amountInput
            .divide(localCurrencyValue, assetInDetail.decimal, RoundingMode.DOWN)
            .movePointRight(assetInDetail.decimal)
            .toBigInteger()
    }
}

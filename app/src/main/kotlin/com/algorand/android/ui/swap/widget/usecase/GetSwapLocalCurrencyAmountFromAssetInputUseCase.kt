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
import com.algorand.android.utils.isZero
import com.algorand.wallet.asset.lite.domain.usecase.GetAssetLiteInformation
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class GetSwapLocalCurrencyAmountFromAssetInputUseCase @Inject constructor(
    private val getUsdToPrimaryFiatConversionRate: GetUsdToPrimaryFiatConversionRate,
    private val getAssetLiteInformation: GetAssetLiteInformation
) : GetSwapLocalCurrencyAmountFromAssetInput {

    override suspend fun invoke(amountInput: BigDecimal, assetId: Long): BigDecimal {
        val assetLite = getAssetLiteInformation(assetId) ?: return BigDecimal.ZERO
        val assetUsdValue = assetLite.usdValue ?: return BigDecimal.ZERO
        val usdToLocalCurrencyRate = getUsdToPrimaryFiatConversionRate()
        if (assetUsdValue.isZero() || usdToLocalCurrencyRate.isZero()) return BigDecimal.ZERO
        val localCurrencyValue = assetUsdValue.multiply(usdToLocalCurrencyRate)
        return amountInput.multiply(localCurrencyValue).setScale(2, RoundingMode.DOWN)
    }
}

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

package com.algorand.android.modules.parity.domain.usecase

import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.parity.domain.model.ParityDisplayValue
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.utils.orZero
import java.math.BigInteger
import javax.inject.Inject

internal class GetParityDisplayValueUseCase @Inject constructor(
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue
) : GetParityDisplayValue {

    override fun invoke(assetLite: AssetLite): ParityDisplayValue {
        return with(assetLite) {
            val selectedParityValue = getSelectedParityValue()
            val secondaryParityValue = getSecondaryParityValue()
            ParityDisplayValue(
                primaryParityValue = getPrimaryParityValue(selectedParityValue, secondaryParityValue),
                secondaryParityValue = secondaryParityValue,
                formattedAmount = amount.formatAmount(decimal),
                formattedCompactAmount = amount.formatAmount(decimal, isCompact = true),
                isAmountInSelectedCurrencyVisible = usdValue != null && amount isGreaterThan BigInteger.ZERO
            )
        }
    }

    private fun AssetLite.getPrimaryParityValue(
        selectedParityValue: ParityValue,
        secondaryParityValue: ParityValue
    ): ParityValue {
        return if (isAlgo && selectedParityValue.selectedCurrencySymbol == Currency.ALGO.symbol) {
            secondaryParityValue
        } else {
            selectedParityValue
        }
    }

    private fun AssetLite.getSelectedParityValue(): ParityValue {
        return getPrimaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
    }

    private fun AssetLite.getSecondaryParityValue(): ParityValue {
        return getSecondaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
    }
}

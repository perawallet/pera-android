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

package com.algorand.android.ui.common.amount

import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS

sealed class PlainFormattedAmount : FormattedAmount {

    protected abstract val amount: PeraAmount
    protected abstract val decimalConfig: DecimalConfig

    override fun getFormattedValue(): String {
        return PeraNumberFormatter(decimalConfig).format(amount.value)
    }

    data class AlgoPlainFormattedAmount(
        override val amount: PeraAmount,
        override val decimalConfig: DecimalConfig
    ) : PlainFormattedAmount() {
        constructor(amount: PeraAmount) : this(
            amount = amount,
            decimalConfig = DecimalConfig(maxDecimals = ALGO_DECIMALS)
        )
    }

    data class SimplePlainFormattedAmount(
        override val amount: PeraAmount,
        override val decimalConfig: DecimalConfig
    ) : PlainFormattedAmount()

    data class FiatPlainFormattedAmount(override val amount: PeraAmount) : PlainFormattedAmount() {
        override val decimalConfig: DecimalConfig = DecimalConfig(maxDecimals = 2)
    }
}

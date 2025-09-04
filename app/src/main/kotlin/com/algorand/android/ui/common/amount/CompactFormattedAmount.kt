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

import android.icu.text.CompactDecimalFormat
import android.icu.text.NumberFormat
import com.algorand.android.ui.common.amount.CompactFormattedAmount.FractionalType.Asset.getMaxFractionalDigit
import com.algorand.android.utils.isLesserThan
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.util.Locale

data class CompactFormattedAmount(
    private val amount: PeraAmount,
    private val fractionalType: FractionalType,
    private val compactType: CompactType = CompactType.SHORT
) : FormattedAmount {

    override fun getFormattedValue(): String {
        return getFormatter().format(amount.value)
    }

    private fun getFormatter(): NumberFormat {
        return CompactDecimalFormat
            .getInstance(Locale.getDefault(), getCompactStyle())
            .apply {
                maximumFractionDigits = fractionalType.getMaxFractionalDigit(amount)
                minimumFractionDigits = getMinFractionalDigit()
            }
    }

    private fun getCompactStyle(): CompactDecimalFormat.CompactStyle {
        return when (compactType) {
            CompactType.SHORT -> CompactDecimalFormat.CompactStyle.SHORT
            CompactType.LONG -> CompactDecimalFormat.CompactStyle.LONG
        }
    }

    private fun getMinFractionalDigit(): Int {
        val minFractionalDigit = 2.takeIf { amount.value isLesserThan fractionalType.minDecimalThreshold }
        return if (minFractionalDigit == null) 0 else minOf(getMaxFractionalDigit(amount), minFractionalDigit)
    }

    sealed interface FractionalType {

        val minDecimalThreshold: BigDecimal

        fun getMaxFractionalDigit(amount: PeraAmount): Int

        data object Fiat : FractionalType {

            override val minDecimalThreshold: BigDecimal
                get() = BigDecimal(1_000_000)

            override fun getMaxFractionalDigit(amount: PeraAmount): Int {
                return if (amount.value isLesserThan ONE) 6 else 2
            }
        }

        data object Asset : FractionalType {

            override val minDecimalThreshold: BigDecimal
                get() = BigDecimal(1_000_000)

            override fun getMaxFractionalDigit(amount: PeraAmount): Int {
                return when {
                    amount.value isLesserThan ONE -> 6
                    amount.value isLesserThan TEN -> 4
                    else -> 2
                }
            }
        }

        data object Collectible : FractionalType {

            override val minDecimalThreshold: BigDecimal
                get() = BigDecimal.ZERO

            override fun getMaxFractionalDigit(amount: PeraAmount): Int = 1
        }
    }

    enum class CompactType {
        SHORT,
        LONG
    }
}

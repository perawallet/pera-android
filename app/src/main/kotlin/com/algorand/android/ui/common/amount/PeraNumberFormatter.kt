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

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

class PeraNumberFormatter(decimalConfig: DecimalConfig) {

    private val formatter = NumberFormat.getInstance().apply {
        roundingMode = RoundingMode.DOWN
        maximumFractionDigits = decimalConfig.maxDecimals
        minimumFractionDigits = getMinimumFractionalDigits(decimalConfig)
    }

    fun format(number: BigDecimal): String {
        return formatter.format(number)
    }

    private fun getMinimumFractionalDigits(decimalConfig: DecimalConfig): Int {
        return when (decimalConfig.minDecimals) {
            is DecimalConfig.MinDecimalType.Fixed -> decimalConfig.minDecimals.value
            is DecimalConfig.MinDecimalType.FixedToMax -> decimalConfig.maxDecimals
            DecimalConfig.MinDecimalType.Default -> TWO_DECIMALS
        }
    }

    private companion object {
        const val TWO_DECIMALS = 2
    }
}

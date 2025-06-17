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
import java.math.BigInteger

data class PeraAmount(val value: BigDecimal) {

    constructor(value: BigInteger, decimal: Int) : this(
        value = value.toBigDecimal().movePointLeft(decimal)
    )

    constructor(value: Long?, decimal: Int) : this(getAmountAsBigDecimal(value, decimal))

    private companion object {
        fun getAmountAsBigDecimal(amount: Long?, decimals: Int): BigDecimal {
            return when {
                amount == null -> BigDecimal.ZERO
                decimals == 0 -> BigDecimal(amount)
                else -> BigDecimal.valueOf(amount, decimals)
            }
        }
    }
}

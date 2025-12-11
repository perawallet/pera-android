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

package com.algorand.wallet.utils

import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode

infix fun BigDecimal.isGreaterThan(other: BigDecimal): Boolean {
    return this.compareTo(other) == 1
}

infix fun BigDecimal.isLesserThan(other: BigDecimal): Boolean {
    return this.compareTo(other) == -1
}

fun String?.toBigDecimalOrZero(): BigDecimal {
    return this?.toBigDecimalOrNull() ?: ZERO
}

fun BigDecimal?.orZero(): BigDecimal {
    return this ?: ZERO
}

fun BigDecimal.isPositive(): Boolean {
    return signum() == 1
}

fun BigDecimal.isNegative(): Boolean {
    return signum() == -1
}

fun BigDecimal.isZero(): Boolean {
    return signum() == 0
}

fun BigDecimal?.divideOrZero(divisor: BigDecimal?, scale: Int, roundingMode: RoundingMode): BigDecimal {
    return if (this == null || divisor == null || divisor.isZero()) {
        ZERO
    } else {
        divide(divisor, scale, roundingMode)
    }
}

fun BigDecimal?.divideOrNull(divisor: BigDecimal?, scale: Int, roundingMode: RoundingMode): BigDecimal? {
    return if (this == null || divisor == null || divisor.isZero()) {
        null
    } else {
        divide(divisor, scale, roundingMode)
    }
}

fun BigDecimal.divideOrZero(divisor: BigDecimal, roundingMode: RoundingMode): BigDecimal {
    return if (divisor.isZero()) {
        ZERO
    } else {
        divide(divisor, roundingMode)
    }
}

fun BigDecimal.multiplyOrZero(multiplier: BigDecimal?): BigDecimal {
    return this.multiply(multiplier ?: ZERO)
}

fun BigDecimal.multiplyOrNull(multiplier: BigDecimal?): BigDecimal? {
    return this.multiply(multiplier ?: return null)
}

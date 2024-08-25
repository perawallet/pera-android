/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.formatting

import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.CollectibleFractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.FractionalDigit
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class FormatAmountByCollectibleFractionalDigitUseCase @Inject constructor() :
    FormatAmountByCollectibleFractionalDigit {

    override fun invoke(amount: BigInteger?, decimals: Int, isDecimalFixed: Boolean, isCompact: Boolean): String {
        return (amount ?: BigInteger.ZERO).toBigDecimal(decimals).formatAmount(
            decimals = decimals,
            isDecimalFixed = isDecimalFixed,
            isCompact = isCompact,
            fractionalDigit = CollectibleFractionalDigit
        )
    }

    private fun BigDecimal.formatAmount(
        decimals: Int,
        isDecimalFixed: Boolean,
        isCompact: Boolean = false,
        minDecimals: Int? = null,
        fractionalDigit: FractionalDigit.FractionalDigitCreator
    ): String {
        return if (isCompact) {
            formatCompactNumber(this, fractionalDigit)
        } else {
            getNumberFormat(decimals, isDecimalFixed, minDecimals).format(this)
        }
    }
}

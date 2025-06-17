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

import com.mitsinsar.peracompactdecimalformat.PeraCompactDecimalFormat
import com.mitsinsar.peracompactdecimalformat.PeraCompactDecimalFormatBuilder
import com.mitsinsar.peracompactdecimalformat.locals.EnglishLocale
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.AssetFractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.CollectibleFractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.FiatFractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.FractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.toPeraDecimal

data class CompactFormattedAmount(
    private val amount: PeraAmount,
    private val type: FractionalType
) : FormattedAmount {

    override fun getFormattedValue(): String {
        return getFormatter()
            .format(amount.value.toPeraDecimal())
            .formattedNumberWithSuffix
    }

    private fun getFormatter(): PeraCompactDecimalFormat {
        return PeraCompactDecimalFormatBuilder.getInstance()
            .setLocale(EnglishLocale)
            .setFractionalDigitCreator(getFractionalDigitCreator())
            .build()
    }

    private fun getFractionalDigitCreator(): FractionalDigit.FractionalDigitCreator {
        return when (type) {
            is FractionalType.Fiat -> FiatFractionalDigit
            is FractionalType.Asset -> AssetFractionalDigit
            is FractionalType.Collectible -> CollectibleFractionalDigit
        }
    }

    sealed interface FractionalType {
        data object Fiat : FractionalType
        data object Asset : FractionalType
        data object Collectible : FractionalType
    }
}

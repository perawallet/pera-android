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

package com.algorand.android.utils

import com.mitsinsar.peracompactdecimalformat.PeraCompactDecimalFormatBuilder
import com.mitsinsar.peracompactdecimalformat.locals.EnglishLocale
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.FractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.toPeraDecimal
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

const val PLUS_SIGN: String = "+"
const val MINUS_SIGN: String = "-"
private const val NUMBER_DECIMAL_FORMAT = "#,###.##"

fun formatCompactNumber(number: BigDecimal, fractionalDigitCreator: FractionalDigit.FractionalDigitCreator): String {
    return PeraCompactDecimalFormatBuilder.getInstance()
        .setLocale(EnglishLocale)
        .setFractionalDigitCreator(fractionalDigitCreator)
        .build()
        .format(number.toPeraDecimal()).formattedNumberWithSuffix
}

fun formatNumberWithDecimalSeparators(number: Number): String? {
    return DecimalFormat(NUMBER_DECIMAL_FORMAT, DecimalFormatSymbols.getInstance(Locale.getDefault())).format(number)
}

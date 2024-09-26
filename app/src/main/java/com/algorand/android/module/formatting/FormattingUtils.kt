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

package com.algorand.android.module.formatting

import android.icu.text.NumberFormat
import com.algorand.android.assetutils.AssetConstants.ALGO_DECIMALS
import com.algorand.android.currency.domain.model.Currency
import com.mitsinsar.peracompactdecimalformat.PeraCompactDecimalFormatBuilder
import com.mitsinsar.peracompactdecimalformat.locals.ChineseLocale
import com.mitsinsar.peracompactdecimalformat.locals.EnglishLocale
import com.mitsinsar.peracompactdecimalformat.locals.FrenchLocale
import com.mitsinsar.peracompactdecimalformat.locals.GermanLocale
import com.mitsinsar.peracompactdecimalformat.locals.ItalianLocale
import com.mitsinsar.peracompactdecimalformat.locals.JapaneseLocale
import com.mitsinsar.peracompactdecimalformat.locals.KoreanLocale
import com.mitsinsar.peracompactdecimalformat.locals.PortugueseLocale
import com.mitsinsar.peracompactdecimalformat.locals.SpanishLocale
import com.mitsinsar.peracompactdecimalformat.locals.TurkishLocale
import com.mitsinsar.peracompactdecimalformat.locals.base.BaseLocale
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.AssetFractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.CollectibleFractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.FiatFractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.fractionaldigit.FractionalDigit
import com.mitsinsar.peracompactdecimalformat.utils.toPeraDecimal
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

const val TWO_DECIMALS = 2
private const val FIAT_MAX_DECIMAL = 6
private const val FIAT_MIN_DECIMAL = 2
private const val DEFAULT_PERCENTAGE_DECIMAL_PRECISION = 3
private const val DECIMAL_PRECISION_MULTIPLIER = 10f
private const val ALGO_AMOUNT_FORMAT = "#,##0.00####"

fun BigDecimal.formatAsCurrency(symbol: String, isCompact: Boolean = false, isFiat: Boolean = false): String {
    val formattedString = if (isCompact) {
        val fractionalDigitCreator = getFractionalDigitCreator(isFiat)
        formatCompactNumber(this, fractionalDigitCreator)
    } else {
        formatAsCurrencyDecimals(symbol)
    }
    return StringBuilder(symbol).append(formattedString).toString()
}

private fun BigDecimal.formatAsCurrencyDecimals(symbol: String): String {
    val numberFormatter = if (symbol == Currency.ALGO.symbol) {
        getNumberFormat(ALGO_DECIMALS)
    } else {
        val decimal = getFiatFormatDecimal(this)
        getNumberFormat(decimal)
    }
    return numberFormatter.format(this)
}

private fun getFiatFormatDecimal(number: BigDecimal): Int {
    return if (number.compareTo(BigDecimal.ONE) == -1) FIAT_MAX_DECIMAL else FIAT_MIN_DECIMAL
}

fun BigInteger?.formatAmount(
    decimals: Int,
    isDecimalFixed: Boolean = false,
    isCompact: Boolean = false,
    isFiat: Boolean = false
): String {
    return (this ?: BigInteger.ZERO).toBigDecimal(decimals)
        .formatAmount(decimals, isDecimalFixed, isCompact, isFiat = isFiat)
}

fun BigDecimal.formatAmount(
    decimals: Int,
    isDecimalFixed: Boolean,
    isCompact: Boolean = false,
    minDecimals: Int? = null,
    isFiat: Boolean = false
): String {
    return if (isCompact) {
        val fractionalDigitCreator = getFractionalDigitCreator(isFiat)
        formatCompactNumber(this, fractionalDigitCreator)
    } else {
        getNumberFormat(decimals, isDecimalFixed, minDecimals).format(this)
    }
}

fun getNumberFormat(
    decimals: Int,
    isDecimalFixed: Boolean = false,
    minDecimals: Int? = null
): NumberFormat {
    return NumberFormat.getInstance().apply {
        roundingMode = RoundingMode.DOWN.ordinal
        maximumFractionDigits = decimals
        minimumFractionDigits = when {
            minDecimals != null -> minDecimals
            isDecimalFixed -> decimals
            else -> TWO_DECIMALS
        }
    }
}

fun String.formatAsAlgoAmount(transactionSign: String? = null): String {
    return transactionSign.orEmpty() + Currency.ALGO.symbol + this
}

fun String.formatAsAssetAmount(assetShortName: String?, transactionSign: String? = null): String {
    return "${transactionSign.orEmpty()}$this $assetShortName"
}

fun getFractionalDigitCreator(isFiat: Boolean): FractionalDigit.FractionalDigitCreator {
    return if (isFiat) FiatFractionalDigit else AssetFractionalDigit
}

fun formatCompactNumber(number: BigDecimal, fractionalDigitCreator: FractionalDigit.FractionalDigitCreator): String {
    return PeraCompactDecimalFormatBuilder.getInstance()
        .setLocale(getPeraCompactDecimalFormatterLocal())
        .setFractionalDigitCreator(fractionalDigitCreator)
        .build()
        .format(number.toPeraDecimal()).formattedNumberWithSuffix
}

fun BigDecimal.formatAsTwoDecimals(): String {
    return getNumberFormat(TWO_DECIMALS).format(this)
}

// TODO: Create a library function for this
private fun getPeraCompactDecimalFormatterLocal(): BaseLocale {
    return when (Locale.getDefault().language.uppercase()) {
        TurkishLocale.localeConstant -> TurkishLocale
        EnglishLocale.localeConstant -> EnglishLocale
        GermanLocale.localeConstant -> GermanLocale
        ChineseLocale.localeConstant -> ChineseLocale
        FrenchLocale.localeConstant -> FrenchLocale
        ItalianLocale.localeConstant -> ItalianLocale
        JapaneseLocale.localeConstant -> JapaneseLocale
        KoreanLocale.localeConstant -> KoreanLocale
        PortugueseLocale.localeConstant -> PortugueseLocale
        SpanishLocale.localeConstant -> SpanishLocale
        else -> EnglishLocale
    }
}

fun BigDecimal.formatAmount(
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

fun BigDecimal?.formatAmountByCollectibleFractionalDigit(
    decimals: Int,
    isDecimalFixed: Boolean = false,
    isCompact: Boolean = false
): String {
    return (this ?: BigDecimal.ZERO).formatAmount(
        decimals = decimals,
        isDecimalFixed = isDecimalFixed,
        isCompact = isCompact,
        fractionalDigit = CollectibleFractionalDigit
    )
}

fun Float.formatAsPercentage(decimalPrecision: Int = DEFAULT_PERCENTAGE_DECIMAL_PRECISION): String {
    val decimalMultiplier = DECIMAL_PRECISION_MULTIPLIER.pow(decimalPrecision)
    val precisionAppliedValue = (this * decimalMultiplier).roundToInt() / decimalMultiplier
    return "${getPercentageFormat(decimalPrecision).format(precisionAppliedValue)}%"
}

private fun getPercentageFormat(decimalPrecision: Int): DecimalFormat {
    return DecimalFormat().apply {
        maximumFractionDigits = decimalPrecision
    }
}

fun BigInteger?.formatAsAlgoString(): String {
    return DecimalFormat(ALGO_AMOUNT_FORMAT, DecimalFormatSymbols()).format(
        BigDecimal.valueOf(this?.toLong() ?: 0L, ALGO_DECIMALS)
    )
}

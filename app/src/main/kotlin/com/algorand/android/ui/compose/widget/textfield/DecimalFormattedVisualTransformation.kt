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

package com.algorand.android.ui.compose.widget.textfield

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DecimalFormattedVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text

        if (original.isBlank()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val symbols = DecimalFormatSymbols.getInstance()
        val decimalSeparator = symbols.decimalSeparator

        // Formatting assumes the input contains only digits and at most the decimal
        // separator. Anything else (e.g. grouping separators from pasted text) would
        // break the offset mapping below, so pass such input through unchanged.
        if (original.any { !it.isDigit() && it != decimalSeparator }) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val decimalSeparatorString = decimalSeparator.toString()
        val parts = original.split(decimalSeparatorString, limit = 2)
        val integerPart = parts[0]
        val decimalPart = parts.getOrNull(1)

        val formattedInteger = try {
            DecimalFormat("#,###", symbols).format(integerPart.toLong())
        } catch (_: NumberFormatException) {
            integerPart
        }

        val result = if (!decimalPart.isNullOrEmpty()) {
            "$formattedInteger$decimalSeparatorString$decimalPart"
        } else {
            "$formattedInteger${decimalSeparatorString.takeIf { original.contains(it) }.orEmpty()}"
        }

        val integerPartLength = integerPart.length
        val formattedIntegerLength = formattedInteger.length
        val groupingSeparator = symbols.groupingSeparator

        val digitPositions = IntArray(integerPartLength + 1)
        var digitIndex = 0
        for (i in formattedInteger.indices) {
            if (formattedInteger[i] != groupingSeparator) {
                digitPositions[digitIndex++] = i
            }
        }
        digitPositions[integerPartLength] = formattedIntegerLength

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset >= original.length) return result.length
                if (offset <= integerPartLength) return digitPositions[offset]
                return formattedIntegerLength + (offset - integerPartLength)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset >= result.length) return original.length
                if (offset <= formattedIntegerLength) {
                    var lo = 0
                    var hi = integerPartLength
                    while (lo < hi) {
                        val mid = (lo + hi + 1) / 2
                        if (digitPositions[mid] <= offset) lo = mid else hi = mid - 1
                    }
                    return lo
                }
                return integerPartLength + (offset - formattedIntegerLength)
            }
        }

        return TransformedText(AnnotatedString(result), offsetMapping)
    }
}

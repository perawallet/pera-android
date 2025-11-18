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
        val decimalSeparator = symbols.decimalSeparator.toString()
        val parts = original.split(decimalSeparator)

        val integerPart = parts.getOrNull(0)?.filter { it.isDigit() }.orEmpty()
        val decimalPart = parts.getOrNull(1)

        val formattedInteger = try {
            DecimalFormat("#,###", symbols).format(integerPart.toLong())
        } catch (_: NumberFormatException) {
            integerPart
        }

        val result = if (!decimalPart.isNullOrEmpty()) {
            "$formattedInteger$decimalSeparator$decimalPart"
        } else {
            "$formattedInteger${decimalSeparator.takeIf { original.contains(it) }.orEmpty()}"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = result.length
            override fun transformedToOriginal(offset: Int): Int = original.length
        }

        return TransformedText(AnnotatedString(result), offsetMapping)
    }
}

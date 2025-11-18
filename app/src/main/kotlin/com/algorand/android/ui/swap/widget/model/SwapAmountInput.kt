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

package com.algorand.android.ui.swap.widget.model

import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

class SwapAmountInput(val locale: Locale) {

    val amountInputFlow: MutableStateFlow<Input> = MutableStateFlow<Input>(Input("", null))
    private val symbols = DecimalFormatSymbols.getInstance(locale)
    private val inputRegex = Regex("^\\d*([.,]?\\d*)$")

    fun setInput(input: String) {
        val amountAsBigDecimal = getInputAsBigDecimal(input)
        val normalizedInput = getNormalizedInput(input) ?: return
        amountInputFlow.value = Input(normalizedInput, amountAsBigDecimal)
    }

    fun setInput(amount: BigDecimal) {
        val localizedAmount = getFormatter().apply { maximumFractionDigits = amount.scale() }.format(amount)
        amountInputFlow.value = Input(localizedAmount, amount)
    }

    private fun getInputAsBigDecimal(input: String): BigDecimal? {
        return try {
            if (input.isBlank()) return null
            val amountNumber = getFormatter().parse(input)?.toString()
            if (!amountNumber.isNullOrBlank()) BigDecimal(amountNumber) else null
        } catch (_: Exception) {
            null
        }
    }

    private fun getFormatter(): NumberFormat = NumberFormat.getInstance(Locale.getDefault())

    private fun getNormalizedInput(input: String): String? {
        val normalizedInput = input.replace(symbols.groupingSeparator, symbols.decimalSeparator)
        return if (inputRegex.matches(normalizedInput)) normalizedInput else null
    }

    data class Input(
        val rawInput: String,
        val amountAsBigDecimal: BigDecimal?
    )
}

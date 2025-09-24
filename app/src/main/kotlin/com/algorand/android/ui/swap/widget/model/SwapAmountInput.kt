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

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow

class SwapAmountInput {

    val amountInputFlow = MutableStateFlow<Input>(Input("", null))

    fun setInput(input: String) {
        val amountAsBigDecimal = getInputAsBigDecimal(input)
        amountInputFlow.value = Input(input, amountAsBigDecimal)
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
        } catch (e: Exception) {
            null
        }
    }

    private fun getFormatter(): NumberFormat = NumberFormat.getInstance(Locale.getDefault())

    data class Input(
        val rawInput: String,
        val amountAsBigDecimal: BigDecimal?
    )
}

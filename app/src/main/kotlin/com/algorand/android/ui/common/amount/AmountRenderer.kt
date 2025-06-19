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

data class AmountRenderer(
    private val formattedAmount: FormattedAmount,
    private val type: RenderType,
    private val prefix: String? = null
) {

    fun getDisplayValue(): String {
        return if (prefix.isNullOrBlank()) {
            getAmount()
        } else {
            "$prefix${getAmount()}"
        }
    }

    private fun getAmount(): String {
        return when (type) {
            is RenderType.Plain -> formattedAmount.getFormattedValue()
            is RenderType.Hidden -> type.pattern
        }
    }

    sealed interface RenderType {
        data object Plain : RenderType
        data class Hidden(val pattern: String = "****") : RenderType
    }
}

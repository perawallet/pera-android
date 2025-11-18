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

package com.algorand.android.ui.swap.confirmation.model

import com.algorand.android.ui.common.amount.AmountRenderer

data class SwapPriceImpact(
    val percentage: AmountRenderer,
    val warningStatus: WarningStatus
) {

    sealed interface WarningStatus {
        data object NoWarning : WarningStatus

        data class Level1(val threshold: Float) : WarningStatus

        data class Level2(val threshold: Float) : WarningStatus
    }
}

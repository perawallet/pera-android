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

package com.algorand.android.module.swap.ui.confirmswap.mapper

import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPriceImpactWarningStatus
import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPriceImpactWarningStatus.NoWarning
import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPriceImpactWarningStatus.Warning
import javax.inject.Inject

internal class ConfirmSwapPriceImpactWarningStatusMapperImpl @Inject constructor() :
    ConfirmSwapPriceImpactWarningStatusMapper {

    override fun invoke(priceImpact: Float): ConfirmSwapPriceImpactWarningStatus {
        return when (priceImpact.toInt()) {
            in NoWarning.percentageRange -> NoWarning
            in Warning.Level1.percentageRange -> Warning.Level1
            in Warning.Level2.percentageRange -> Warning.Level2
            else -> Warning.Level3
        }
    }
}

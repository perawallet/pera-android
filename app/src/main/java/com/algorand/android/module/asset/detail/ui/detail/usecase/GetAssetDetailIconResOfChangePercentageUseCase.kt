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

package com.algorand.android.module.asset.detail.ui.detail.usecase

import com.algorand.android.R
import com.algorand.android.module.foundation.common.isNegative
import com.algorand.android.module.foundation.common.isPositive
import java.math.BigDecimal
import javax.inject.Inject

internal class GetAssetDetailIconResOfChangePercentageUseCase @Inject constructor() :
    GetAssetDetailIconResOfChangePercentage {

    override fun invoke(last24HoursChange: BigDecimal?): Int? {
        return when {
            last24HoursChange == null -> null
            last24HoursChange.isPositive() -> R.drawable.ic_positive_market
            last24HoursChange.isNegative() -> R.drawable.ic_negative_market
            else -> null
        }
    }
}

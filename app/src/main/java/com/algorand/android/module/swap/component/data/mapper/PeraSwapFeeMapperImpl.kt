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

package com.algorand.android.module.swap.component.data.mapper

import com.algorand.android.assetutils.AssetConstants.ALGO_DECIMALS
import com.algorand.android.module.swap.component.data.SwapUtils.DEFAULT_PERA_SWAP_FEE
import com.algorand.android.module.swap.component.data.model.PeraFeeResponse
import com.algorand.android.module.swap.component.domain.model.PeraFee
import javax.inject.Inject

internal class PeraSwapFeeMapperImpl @Inject constructor() : PeraSwapFeeMapper {

    override fun invoke(response: PeraFeeResponse): PeraFee {
        val amount = response.peraFeeAmount
            ?.toBigDecimal()
            ?.movePointLeft(ALGO_DECIMALS)
            ?: DEFAULT_PERA_SWAP_FEE
        return PeraFee(amount)
    }
}

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

import com.algorand.android.module.asset.utils.AssetConstants.ALGO_ID
import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.GetAlgoAmountValue
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class DisplayedCurrencyParityValueMapperImpl @Inject constructor(
    private val getAlgoAmountValue: GetAlgoAmountValue,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue
) : DisplayedCurrencyParityValueMapper {

    override fun invoke(
        assetAmount: BigInteger,
        assetUsdValue: BigDecimal,
        assetDecimal: Int,
        assetId: Long
    ): ParityValue {
        return if (assetId == ALGO_ID) {
            getAlgoAmountValue(assetAmount).parityValueInSelectedCurrency
        } else {
            getPrimaryCurrencyAssetParityValue(
                usdValue = assetUsdValue,
                decimals = assetDecimal,
                amount = assetAmount
            )
        }
    }
}

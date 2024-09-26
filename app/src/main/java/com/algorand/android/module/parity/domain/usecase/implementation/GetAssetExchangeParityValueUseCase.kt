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

package com.algorand.android.module.parity.domain.usecase.implementation

import com.algorand.android.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.GetAssetExchangeParityValue
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryAlgoParityValue
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryAlgoParityValue
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class GetAssetExchangeParityValueUseCase @Inject constructor(
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getPrimaryAlgoParityValue: GetPrimaryAlgoParityValue,
    private val getSecondaryAlgoParityValue: GetSecondaryAlgoParityValue,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue
) : GetAssetExchangeParityValue {

    override fun invoke(isAlgo: Boolean, usdValue: BigDecimal, decimals: Int): ParityValue {
        return if (isAlgo) {
            getAlgoParityValue(decimals)
        } else {
            getAssetParityValue(usdValue, decimals)
        }
    }

    private fun getAlgoParityValue(decimals: Int): ParityValue {
        val oneAlgoAsBigInt = createOneSelectedAssetInBigInteger(decimals)
        return if (isPrimaryCurrencyAlgo()) {
            getSecondaryAlgoParityValue(oneAlgoAsBigInt)
        } else {
            getPrimaryAlgoParityValue(oneAlgoAsBigInt)
        }
    }

    private fun getAssetParityValue(usdValue: BigDecimal, decimals: Int): ParityValue {
        return getPrimaryCurrencyAssetParityValue(usdValue, decimals, createOneSelectedAssetInBigInteger(decimals))
    }

    private fun createOneSelectedAssetInBigInteger(decimals: Int): BigInteger {
        return BigDecimal.ONE.movePointRight(decimals).toBigInteger()
    }
}

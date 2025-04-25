/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.parity.domain.usecase

import com.algorand.android.modules.parity.domain.model.AlgoAmountValue
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.modules.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.utils.CacheResult
import java.math.BigDecimal
import java.math.BigInteger
import kotlinx.coroutines.flow.Flow

fun interface GetAlgoToUsdConversionRate {
    operator fun invoke(): BigDecimal
}

fun interface GetUsdToAlgoConversionRate {
    operator fun invoke(): BigDecimal
}

fun interface GetAlgoAmountValue {
    operator fun invoke(algoAmount: BigInteger): AlgoAmountValue
}

fun interface GetPrimaryCurrencyAssetParityValue {
    operator fun invoke(assetAmount: BigInteger, assetUsdValue: BigDecimal, decimals: Int): ParityValue
}

fun interface GetSecondaryCurrencyAssetParityValue {
    operator fun invoke(assetAmount: BigInteger, assetUsdValue: BigDecimal, decimals: Int): ParityValue
}

fun interface GetPrimaryAlgoParityValue {
    operator fun invoke(algoAmount: BigInteger): ParityValue
}

fun interface GetSecondaryAlgoParityValue {
    operator fun invoke(algoAmount: BigInteger): ParityValue
}

fun interface GetUsdToPrimaryCurrencyConversionRate {
    operator fun invoke(): BigDecimal
}

fun interface GetUsdToSecondaryCurrencyConversionRate {
    operator fun invoke(): BigDecimal
}

internal fun interface CalculateParityValue {
    operator fun invoke(
        assetUsdValue: BigDecimal,
        assetDecimals: Int,
        amount: BigInteger,
        conversionRate: BigDecimal,
        currencySymbol: String
    ): ParityValue
}

fun interface GetSelectedCurrencyDetailFlow {
    operator fun invoke(): Flow<CacheResult<SelectedCurrencyDetail>?>
}

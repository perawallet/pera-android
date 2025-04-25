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

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.modules.accountcore.domain.mapper.AlgoAssetDataMapper
import com.algorand.android.modules.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryAlgoParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryAlgoParityValue
import java.math.BigInteger
import javax.inject.Inject

internal class CreateAlgoOwnedAssetDataUseCase @Inject constructor(
    private val algoAssetDataMapper: AlgoAssetDataMapper,
    private val getPrimaryAlgoParityValue: GetPrimaryAlgoParityValue,
    private val getSecondaryAlgoParityValue: GetSecondaryAlgoParityValue,
    private val getAlgoToUsdConversionRate: GetAlgoToUsdConversionRate,
) : CreateAlgoOwnedAssetData {

    override suspend fun invoke(amount: BigInteger): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData {
        return algoAssetDataMapper(
            amount,
            getPrimaryAlgoParityValue(amount),
            getSecondaryAlgoParityValue(amount),
            usdValue = getAlgoToUsdConversionRate()
        )
    }
}

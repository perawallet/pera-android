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

package com.algorand.android.modules.accountcore.domain.mapper

import com.algorand.android.assetsearch.domain.model.VerificationTier
import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.utils.formatAmount
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_FULL_NAME
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_SHORT_NAME
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class AlgoAssetDataMapperImpl @Inject constructor() : AlgoAssetDataMapper {

    override fun invoke(
        amount: BigInteger,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        usdValue: BigDecimal
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData {
        return BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData(
            id = ALGO_ID,
            name = ALGO_FULL_NAME,
            shortName = ALGO_SHORT_NAME,
            amount = amount,
            formattedAmount = amount.formatAmount(ALGO_DECIMALS),
            formattedCompactAmount = amount.formatAmount(ALGO_DECIMALS, isCompact = true),
            isAlgo = true,
            decimals = ALGO_DECIMALS,
            creatorPublicKey = "",
            usdValue = usdValue,
            isAmountInSelectedCurrencyVisible = true, // Algo always has a currency value
            parityValueInSelectedCurrency = parityValueInSelectedCurrency,
            parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
            prismUrl = null, // Algo does not have prism url
            verificationTier = VerificationTier.TRUSTED,
            optedInAtRound = null
        )
    }
}

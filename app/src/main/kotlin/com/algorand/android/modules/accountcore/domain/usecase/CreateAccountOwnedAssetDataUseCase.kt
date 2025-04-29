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

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.modules.accountcore.domain.mapper.OwnedAssetDataMapper
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.orZero
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.asset.lite.domain.model.AssetDetailLite
import javax.inject.Inject

internal class CreateAccountOwnedAssetDataUseCase @Inject constructor(
    private val ownedAssetDataMapper: OwnedAssetDataMapper,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
) : CreateAccountOwnedAssetData {

    override suspend fun invoke(assetDetailLite: AssetDetailLite, assetHolding: AssetHolding): OwnedAssetData {
        val amount = assetHolding.amount
        return ownedAssetDataMapper(
            assetDetailLite,
            amount = amount,
            formattedAmount = amount.formatAmount(assetDetailLite.decimals),
            formattedCompactAmount = amount.formatAmount(
                assetDetailLite.decimals,
                isCompact = true
            ),
            parityValueInSelectedCurrency = getParityValueInSelectedCurrency(assetDetailLite, assetHolding),
            parityValueInSecondaryCurrency = getParityValueInSecondaryCurrency(assetDetailLite, assetHolding),
            optedInAtRound = assetHolding.optedInAtRound
        )
    }

    private fun getParityValueInSelectedCurrency(
        assetDetailLite: AssetDetailLite,
        assetHolding: AssetHolding
    ): ParityValue {
        return getPrimaryCurrencyAssetParityValue(
            assetHolding.amount,
            assetDetailLite.usdValue.orZero(),
            assetDetailLite.decimals
        )
    }

    private fun getParityValueInSecondaryCurrency(
        assetDetailLite: AssetDetailLite,
        assetHolding: AssetHolding
    ): ParityValue {
        return getSecondaryCurrencyAssetParityValue(
            assetHolding.amount,
            assetDetailLite.usdValue.orZero(),
            assetDetailLite.decimals
        )
    }
}

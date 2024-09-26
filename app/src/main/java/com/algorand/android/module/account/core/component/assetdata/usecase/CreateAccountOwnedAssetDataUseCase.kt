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

package com.algorand.android.module.account.core.component.assetdata.usecase

import com.algorand.android.accountinfo.component.domain.model.AssetHolding
import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.account.core.component.domain.mapper.OwnedAssetDataMapper
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.formatting.formatAmount
import com.algorand.android.parity.domain.model.ParityValue
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencyAssetParityValue
import java.math.BigDecimal
import javax.inject.Inject

internal class CreateAccountOwnedAssetDataUseCase @Inject constructor(
    private val ownedAssetDataMapper: OwnedAssetDataMapper,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
) : CreateAccountOwnedAssetData {

    override suspend fun invoke(assetDetail: AssetDetail, assetHolding: AssetHolding): OwnedAssetData {
        return ownedAssetDataMapper(
            assetDetail,
            amount = assetHolding.amount,
            formattedAmount = assetHolding.amount.formatAmount(assetDetail.getDecimalsOrZero()),
            formattedCompactAmount = assetHolding.amount.formatAmount(
                assetDetail.getDecimalsOrZero(),
                isCompact = true
            ),
            parityValueInSelectedCurrency = getParityValueInSelectedCurrency(assetDetail, assetHolding),
            parityValueInSecondaryCurrency = getParityValueInSecondaryCurrency(assetDetail, assetHolding),
            optedInAtRound = assetHolding.optedInAtRound
        )
    }

    private fun getParityValueInSelectedCurrency(assetDetail: AssetDetail, assetHolding: AssetHolding): ParityValue {
        val safeUsdValue = assetDetail.usdValue ?: BigDecimal.ZERO
        return getPrimaryCurrencyAssetParityValue(
            safeUsdValue,
            assetDetail.getDecimalsOrZero(),
            assetHolding.amount
        )
    }

    private fun getParityValueInSecondaryCurrency(assetDetail: AssetDetail, assetHolding: AssetHolding): ParityValue {
        val safeUsdValue = assetDetail.usdValue ?: BigDecimal.ZERO
        return getSecondaryCurrencyAssetParityValue(
            safeUsdValue,
            assetDetail.getDecimalsOrZero(),
            assetHolding.amount
        )
    }
}

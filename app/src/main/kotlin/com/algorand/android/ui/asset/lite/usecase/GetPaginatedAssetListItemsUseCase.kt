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

package com.algorand.android.ui.asset.lite.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.CompactFormattedAmount
import com.algorand.android.ui.common.amount.CompactFormattedAmount.FractionalType
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.model.AssetLite.Type
import com.algorand.wallet.asset.domain.usecase.GetAssetCollectibleLitesFlow
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPaginatedAssetListItemsUseCase @Inject constructor(
    private val getAssetCollectibleLitesFlow: GetAssetCollectibleLitesFlow,
    private val verificationTierMapper: VerificationTierConfigurationDecider,
    private val assetIconDrawableMapper: AssetIconDrawableMapper,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo
) : GetPaginatedAssetListItems {

    override fun invoke(query: AssetCollectibleLiteQuery): Flow<PagingData<AssetListItem>> {
        return getAssetCollectibleLitesFlow(query).map { pagingData ->
            pagingData.map { assetLite ->
                assetLite.toAssetListItem()
            }
        }
    }

    private fun AssetLite.toAssetListItem(): AssetListItem {
        return AssetListItem(
            assetId = this.assetId,
            name = this.name,
            unitName = this.shortName,
            balance = getBalance(this),
            verificationTier = verificationTierMapper.decideVerificationTierConfiguration(verificationTier),
            assetIcon = assetIconDrawableMapper.map(this)
        )
    }

    private fun getBalance(assetLite: AssetLite): AssetListItem.Balance {
        return AssetListItem.Balance(
            amount = PeraAmount(assetLite.amount, assetLite.decimal),
            primaryAmountRenderer = getPrimaryAmountRenderer(assetLite),
            secondaryAmountRenderer = getSecondaryAmountRenderer(assetLite),
            usdValue = assetLite.usdValue?.let { PeraAmount(it) }
        )
    }

    private fun getPrimaryAmountRenderer(assetLite: AssetLite): AmountRenderer {
        return with(assetLite) {
            val amount = PeraAmount(amount, decimal)
            val fractionalType = if (assetLite.type is Type.Asset) FractionalType.Asset else FractionalType.Collectible
            val formattedAmount = CompactFormattedAmount(amount, fractionalType)
            AmountRenderer(
                formattedAmount,
                type = AmountRenderer.RenderType.Plain,
                prefix = Currency.ALGO.symbol.takeIf { assetId == ALGO_ID }
            )
        }
    }

    private fun getSecondaryAmountRenderer(assetLite: AssetLite): AmountRenderer {
        return with(assetLite) {
            val safeUsdValue = usdValue ?: BigDecimal.ZERO
            val parityValue = if (isAlgo && isPrimaryCurrencyAlgo()) {
                getSecondaryCurrencyAssetParityValue(amount, safeUsdValue, decimal)
            } else {
                getPrimaryCurrencyAssetParityValue(amount, safeUsdValue, decimal)
            }
            val amount = PeraAmount(parityValue.amountAsCurrency)
            val fractionalType = if (isPrimaryCurrencyAlgo()) FractionalType.Fiat else FractionalType.Asset
            val formattedAmount = CompactFormattedAmount(amount, fractionalType)
            AmountRenderer(
                formattedAmount,
                type = AmountRenderer.RenderType.Plain,
                prefix = parityValue.selectedCurrencySymbol
            )
        }
    }
}

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

package com.algorand.android.ui.asset.lite.mapper

import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.ui.asset.lite.model.AssetListItemBalancePayload
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.CompactFormattedAmount
import com.algorand.android.ui.common.amount.CompactFormattedAmount.FractionalType
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.wallet.asset.domain.model.AssetLite.Type
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigDecimal
import javax.inject.Inject

internal class DefaultAssetListItemBalanceMapper @Inject constructor(
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue
) : AssetListItemBalanceMapper {

    override fun invoke(payload: AssetListItemBalancePayload): AssetListItem.Balance {
        return AssetListItem.Balance(
            amount = PeraAmount(payload.amount, payload.decimal),
            primaryAmountRenderer = getPrimaryAmountRenderer(payload),
            secondaryAmountRenderer = getSecondaryAmountRenderer(payload),
            usdValue = payload.usdValue?.let { PeraAmount(it) }
        )
    }

    private fun getPrimaryAmountRenderer(payload: AssetListItemBalancePayload): AmountRenderer {
        val amount = PeraAmount(payload.amount, payload.decimal)
        val fractionalType = if (payload.type is Type.Asset) FractionalType.Asset else FractionalType.Collectible
        val formattedAmount = CompactFormattedAmount(amount, fractionalType)
        return AmountRenderer(
            formattedAmount,
            type = AmountRenderer.RenderType.Plain,
            prefix = Currency.ALGO.symbol.takeIf { payload.assetId == ALGO_ID }
        )
    }

    private fun getSecondaryAmountRenderer(payload: AssetListItemBalancePayload): AmountRenderer {
        return with(payload) {
            val safeUsdValue = usdValue ?: BigDecimal.ZERO
            val parityValue = if (assetId == ALGO_ID && isPrimaryCurrencyAlgo()) {
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

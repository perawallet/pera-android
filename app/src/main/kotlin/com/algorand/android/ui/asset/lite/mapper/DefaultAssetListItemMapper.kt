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

import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.asset.lite.model.AssetListItemBalancePayload
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.wallet.account.lite.domain.model.AssetHoldingLite
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.model.AssetLite.Type.Asset
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset
import javax.inject.Inject

internal class DefaultAssetListItemMapper @Inject constructor(
    private val verificationTierMapper: VerificationTierConfigurationDecider,
    private val assetIconDrawableMapper: AssetIconDrawableMapper,
    private val assetListItemBalanceMapper: AssetListItemBalanceMapper
) : AssetListItemMapper {

    override fun invoke(assetLite: AssetLite): AssetListItem {
        return with(assetLite) {
            val balancePayload = AssetListItemBalancePayload(assetId, amount, decimal, usdValue, type)
            AssetListItem(
                assetId = this.assetId,
                name = this.name,
                unitName = this.shortName,
                balance = assetListItemBalanceMapper(balancePayload),
                verificationTier = verificationTierMapper.decideVerificationTierConfiguration(verificationTier),
                assetIcon = assetIconDrawableMapper.map(this),
                isFavorite = assetLite.isFavorite
            )
        }
    }

    override fun invoke(
        assetHoldings: AssetHoldingLite,
        asset: AvailableSwapAsset,
        isFavorite: Boolean?
    ): AssetListItem {
        val userBalance = assetHoldings.assetHoldingAmounts[asset.assetId]
        val balancePayload = if (userBalance == null) {
            null
        } else {
            with(asset) {
                AssetListItemBalancePayload(assetId, userBalance, decimals, usdValue, Asset(logoUrl))
            }
        }
        return AssetListItem(
            assetId = asset.assetId,
            name = asset.name,
            unitName = asset.unitName,
            balance = balancePayload?.let { assetListItemBalanceMapper(it) },
            verificationTier = verificationTierMapper.decideVerificationTierConfiguration(asset.verificationTier),
            assetIcon = AssetIconDrawable.AssetDrawable(asset.logoUrl.orEmpty(), asset.unitName),
            isFavorite = isFavorite ?: false
        )
    }
}

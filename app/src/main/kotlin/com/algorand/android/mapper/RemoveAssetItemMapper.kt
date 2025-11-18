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

package com.algorand.android.mapper

import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.modules.assets.core.ui.domain.usecase.GetAssetName
import com.algorand.android.modules.parity.domain.usecase.GetParityDisplayValue
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.asset.remove.model.BaseRemoveAssetItem.RemoveAssetItem
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.account.info.domain.model.AssetStatus.PENDING_FOR_ADDITION
import com.algorand.wallet.account.info.domain.model.AssetStatus.PENDING_FOR_REMOVAL
import com.algorand.wallet.asset.domain.model.AssetLite
import javax.inject.Inject

class RemoveAssetItemMapper @Inject constructor(
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val getAssetName: GetAssetName,
    private val getParityDisplayValue: GetParityDisplayValue
) {

    fun mapToRemoveAssetItem(assetLite: AssetLite): RemoveAssetItem {
        return with(assetLite) {
            val parityDisplayValue = getParityDisplayValue(assetLite)
            RemoveAssetItem(
                id = assetId,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
                amount = amount,
                decimals = decimal,
                formattedAmount = parityDisplayValue.formattedAmount,
                formattedCompactAmount = parityDisplayValue.formattedCompactAmount,
                formattedSelectedCurrencyValue = parityDisplayValue.primaryParityValue.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = if (parityDisplayValue.isAmountInSelectedCurrencyVisible) {
                    parityDisplayValue.primaryParityValue.getFormattedCompactValue()
                } else {
                    null
                },
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                actionItemButtonState = getRemoveAssetItemActionButtonState(assetStatus),
                amountInPrimaryCurrency = parityDisplayValue.primaryParityValue.amountAsCurrency,
                type = mapToRemoveAssetItemType(assetLite),
                isFavorite = assetLite.isFavorite
            )
        }
    }

    private fun mapToRemoveAssetItemType(assetLite: AssetLite): RemoveAssetItem.RemoveAssetItemType {
        return when (assetLite.type) {
            is AssetLite.Type.Asset -> {
                RemoveAssetItem.RemoveAssetItemType.Asset(
                    verificationTierConfigurationDecider.decideVerificationTierConfiguration(assetLite.verificationTier)
                )
            }

            is AssetLite.Type.Collectible -> RemoveAssetItem.RemoveAssetItemType.Collectible
        }
    }

    private fun getRemoveAssetItemActionButtonState(assetHoldingStatus: AssetStatus?): AccountAssetItemButtonState {
        return when (assetHoldingStatus) {
            PENDING_FOR_REMOVAL, PENDING_FOR_ADDITION -> AccountAssetItemButtonState.PROGRESS
            else -> AccountAssetItemButtonState.REMOVAL
        }
    }
}

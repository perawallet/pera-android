/*
 * Copyright 2022 Pera Wallet, LDA
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

import androidx.annotation.StringRes
import com.algorand.android.accountcore.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleAudioData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleImageData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleMixedData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleUnsupportedData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleVideoData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.models.BaseRemoveAssetItem
import com.algorand.android.models.BaseRemoveAssetItem.BaseRemovableItem.BaseRemoveCollectibleItem.RemoveCollectibleAudioItem
import com.algorand.android.models.BaseRemoveAssetItem.BaseRemovableItem.BaseRemoveCollectibleItem.RemoveCollectibleImageItem
import com.algorand.android.models.BaseRemoveAssetItem.BaseRemovableItem.BaseRemoveCollectibleItem.RemoveCollectibleMixedItem
import com.algorand.android.models.BaseRemoveAssetItem.BaseRemovableItem.BaseRemoveCollectibleItem.RemoveCollectibleVideoItem
import com.algorand.android.models.BaseRemoveAssetItem.BaseRemovableItem.BaseRemoveCollectibleItem.RemoveNotSupportedCollectibleItem
import com.algorand.android.models.BaseRemoveAssetItem.BaseRemovableItem.RemoveAssetItem
import com.algorand.android.models.BaseRemoveAssetItem.DescriptionViewItem
import com.algorand.android.models.BaseRemoveAssetItem.SearchViewItem
import com.algorand.android.models.BaseRemoveAssetItem.TitleViewItem
import com.algorand.android.models.ScreenState
import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.utils.AssetName
import javax.inject.Inject

class RemoveAssetItemMapper @Inject constructor(
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper
) {

    suspend fun mapToRemoveAssetItem(
        ownedAssetData: OwnedAssetData,
        actionItemButtonState: AccountAssetItemButtonState
    ): RemoveAssetItem {
        return with(ownedAssetData) {
            RemoveAssetItem(
                id = id,
                name = AssetName.create(name),
                shortName = AssetName.create(shortName),
                amount = amount,
                creatorPublicKey = creatorPublicKey,
                decimals = decimals,
                formattedAmount = formattedAmount,
                formattedCompactAmount = formattedCompactAmount,
                formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = if (isAmountInSelectedCurrencyVisible) {
                    parityValueInSelectedCurrency.getFormattedCompactValue()
                } else {
                    null
                },
                verificationTierConfiguration = verificationTierConfigurationMapper(verificationTier),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(id),
                actionItemButtonState = actionItemButtonState,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    suspend fun mapToRemoveCollectibleImageItem(
        ownedCollectibleImageData: OwnedCollectibleImageData,
        actionItemButtonState: AccountAssetItemButtonState
    ): RemoveCollectibleImageItem {
        return with(ownedCollectibleImageData) {
            RemoveCollectibleImageItem(
                id = id,
                name = AssetName.create(name),
                shortName = AssetName.create(shortName),
                amount = amount,
                creatorPublicKey = creatorPublicKey,
                decimals = decimals,
                formattedAmount = formattedAmount,
                formattedCompactAmount = formattedCompactAmount,
                formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = if (isAmountInSelectedCurrencyVisible) {
                    parityValueInSelectedCurrency.getFormattedCompactValue()
                } else {
                    null
                },
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(id),
                actionItemButtonState = actionItemButtonState,
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    suspend fun mapToRemoveCollectibleVideoItem(
        ownedCollectibleImageData: OwnedCollectibleVideoData,
        actionItemButtonState: AccountAssetItemButtonState
    ): RemoveCollectibleVideoItem {
        return with(ownedCollectibleImageData) {
            RemoveCollectibleVideoItem(
                id = id,
                name = AssetName.create(name),
                shortName = AssetName.create(shortName),
                amount = amount,
                creatorPublicKey = creatorPublicKey,
                decimals = decimals,
                formattedAmount = formattedAmount,
                formattedCompactAmount = formattedCompactAmount,
                formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = if (isAmountInSelectedCurrencyVisible) {
                    parityValueInSelectedCurrency.getFormattedCompactValue()
                } else {
                    null
                },
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(id),
                actionItemButtonState = actionItemButtonState,
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    suspend fun mapTo(
        ownedCollectibleAudioData: OwnedCollectibleAudioData,
        actionItemButtonState: AccountAssetItemButtonState
    ): RemoveCollectibleAudioItem {
        return with(ownedCollectibleAudioData) {
            RemoveCollectibleAudioItem(
                id = id,
                name = AssetName.create(name),
                shortName = AssetName.create(shortName),
                amount = amount,
                creatorPublicKey = creatorPublicKey,
                decimals = decimals,
                formattedAmount = formattedAmount,
                formattedCompactAmount = formattedCompactAmount,
                formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = parityValueInSelectedCurrency.getFormattedCompactValue(),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(id),
                actionItemButtonState = actionItemButtonState,
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    suspend fun mapToRemoveCollectibleMixedItem(
        ownedCollectibleMixedData: OwnedCollectibleMixedData,
        actionItemButtonState: AccountAssetItemButtonState
    ): RemoveCollectibleMixedItem {
        return with(ownedCollectibleMixedData) {
            RemoveCollectibleMixedItem(
                id = id,
                name = AssetName.create(name),
                shortName = AssetName.create(shortName),
                amount = amount,
                creatorPublicKey = creatorPublicKey,
                decimals = decimals,
                formattedAmount = formattedAmount,
                formattedCompactAmount = formattedCompactAmount,
                formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = if (isAmountInSelectedCurrencyVisible) {
                    parityValueInSelectedCurrency.getFormattedCompactValue()
                } else {
                    null
                },
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(id),
                actionItemButtonState = actionItemButtonState,
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    suspend fun mapToRemoveNotSupportedCollectibleItem(
        ownedUnsupportedCollectibleData: OwnedCollectibleUnsupportedData,
        actionItemButtonState: AccountAssetItemButtonState
    ): RemoveNotSupportedCollectibleItem {
        return with(ownedUnsupportedCollectibleData) {
            RemoveNotSupportedCollectibleItem(
                id = id,
                name = AssetName.create(name),
                shortName = AssetName.create(shortName),
                amount = amount,
                creatorPublicKey = creatorPublicKey,
                decimals = decimals,
                formattedAmount = formattedAmount,
                formattedCompactAmount = formattedCompactAmount,
                formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = if (isAmountInSelectedCurrencyVisible) {
                    parityValueInSelectedCurrency.getFormattedCompactValue()
                } else {
                    null
                },
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(id),
                actionItemButtonState = actionItemButtonState,
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    fun mapToTitleItem(@StringRes titleTextRes: Int): TitleViewItem {
        return TitleViewItem(titleTextRes)
    }

    fun mapToDescriptionItem(@StringRes descriptionTextRes: Int): DescriptionViewItem {
        return DescriptionViewItem(descriptionTextRes)
    }

    fun mapToSearchItem(@StringRes searchViewHintResId: Int): SearchViewItem {
        return SearchViewItem(searchViewHintResId)
    }

    fun mapToScreenStateItem(screenState: ScreenState): BaseRemoveAssetItem.ScreenStateItem {
        return BaseRemoveAssetItem.ScreenStateItem(screenState)
    }
}

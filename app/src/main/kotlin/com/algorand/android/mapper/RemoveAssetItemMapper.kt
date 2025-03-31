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
import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleAudioData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleImageData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleMixedData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleVideoData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedUnsupportedCollectibleData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
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
import com.algorand.android.modules.assets.core.ui.domain.usecase.GetAssetName
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.account.info.domain.model.AssetStatus.PENDING_FOR_ADDITION
import com.algorand.wallet.account.info.domain.model.AssetStatus.PENDING_FOR_REMOVAL
import javax.inject.Inject

class RemoveAssetItemMapper @Inject constructor(
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val getAssetName: GetAssetName
) {

    fun mapToRemovableItem(
        ownedCollectible: BaseOwnedCollectibleData,
        assetHoldingStatus: AssetStatus?
    ): BaseRemoveAssetItem.BaseRemovableItem {
        return when (ownedCollectible) {
            is OwnedCollectibleImageData -> mapToCollectibleImageItem(ownedCollectible, assetHoldingStatus)
            is OwnedUnsupportedCollectibleData -> mapToNotSupportedCollectibleItem(ownedCollectible, assetHoldingStatus)
            is OwnedCollectibleVideoData -> mapToCollectibleVideoItem(ownedCollectible, assetHoldingStatus)
            is OwnedCollectibleMixedData -> mapToCollectibleMixedItem(ownedCollectible, assetHoldingStatus)
            is OwnedCollectibleAudioData -> mapToCollectibleAudioItem(ownedCollectible, assetHoldingStatus)
        }
    }

    fun mapToRemoveAssetItem(
        ownedAssetData: OwnedAssetData,
        assetHoldingStatus: AssetStatus?
    ): RemoveAssetItem {
        return with(ownedAssetData) {
            RemoveAssetItem(
                id = id,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
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
                verificationTierConfiguration =
                verificationTierConfigurationDecider.decideVerificationTierConfiguration(verificationTier),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                actionItemButtonState = getRemoveAssetItemActionButtonState(assetHoldingStatus),
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    private fun mapToCollectibleImageItem(
        ownedCollectibleImageData: OwnedCollectibleImageData,
        assetHoldingStatus: AssetStatus?
    ): RemoveCollectibleImageItem {
        return with(ownedCollectibleImageData) {
            RemoveCollectibleImageItem(
                id = id,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
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
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                actionItemButtonState = getRemoveAssetItemActionButtonState(assetHoldingStatus),
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    private fun mapToCollectibleVideoItem(
        ownedCollectibleImageData: OwnedCollectibleVideoData,
        assetHoldingStatus: AssetStatus?
    ): RemoveCollectibleVideoItem {
        return with(ownedCollectibleImageData) {
            RemoveCollectibleVideoItem(
                id = id,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
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
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                actionItemButtonState = getRemoveAssetItemActionButtonState(assetHoldingStatus),
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    private fun mapToCollectibleAudioItem(
        ownedCollectibleAudioData: OwnedCollectibleAudioData,
        assetHoldingStatus: AssetStatus?
    ): RemoveCollectibleAudioItem {
        return with(ownedCollectibleAudioData) {
            RemoveCollectibleAudioItem(
                id = id,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
                amount = amount,
                creatorPublicKey = creatorPublicKey,
                decimals = decimals,
                formattedAmount = formattedAmount,
                formattedCompactAmount = formattedCompactAmount,
                formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
                formattedSelectedCurrencyCompactValue = parityValueInSelectedCurrency.getFormattedCompactValue(),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                actionItemButtonState = getRemoveAssetItemActionButtonState(assetHoldingStatus),
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    private fun mapToCollectibleMixedItem(
        ownedCollectibleMixedData: OwnedCollectibleMixedData,
        assetHoldingStatus: AssetStatus?
    ): RemoveCollectibleMixedItem {
        return with(ownedCollectibleMixedData) {
            RemoveCollectibleMixedItem(
                id = id,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
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
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                actionItemButtonState = getRemoveAssetItemActionButtonState(assetHoldingStatus),
                optedInAtRound = optedInAtRound,
                amountInPrimaryCurrency = parityValueInSelectedCurrency.amountAsCurrency
            )
        }
    }

    private fun mapToNotSupportedCollectibleItem(
        ownedUnsupportedCollectibleData: OwnedUnsupportedCollectibleData,
        assetHoldingStatus: AssetStatus?
    ): RemoveNotSupportedCollectibleItem {
        return with(ownedUnsupportedCollectibleData) {
            RemoveNotSupportedCollectibleItem(
                id = id,
                name = getAssetName(name),
                shortName = getAssetName(shortName),
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
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                actionItemButtonState = getRemoveAssetItemActionButtonState(assetHoldingStatus),
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

    private fun getRemoveAssetItemActionButtonState(assetHoldingStatus: AssetStatus?): AccountAssetItemButtonState {
        return when (assetHoldingStatus) {
            PENDING_FOR_REMOVAL, PENDING_FOR_ADDITION -> AccountAssetItemButtonState.PROGRESS
            else -> AccountAssetItemButtonState.REMOVAL
        }
    }
}

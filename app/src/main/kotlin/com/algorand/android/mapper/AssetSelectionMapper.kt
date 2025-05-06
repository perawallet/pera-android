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

import androidx.paging.PagingData
import androidx.paging.map
import com.algorand.android.customviews.accountandassetitem.mapper.AssetItemConfigurationMapper
import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.ui.asset.lite.extension.getFormattedAmount
import com.algorand.android.ui.asset.lite.extension.getFormattedCompactAmount
import com.algorand.android.ui.asset.lite.extension.isAmountInSelectedCurrencyVisible
import com.algorand.android.ui.asset.selection.view.model.BaseSelectAssetItem
import com.algorand.android.ui.asset.selection.view.model.BaseSelectAssetItem.SelectAssetItem
import com.algorand.android.ui.asset.selection.view.model.BaseSelectAssetItem.SelectCollectibleItem
import com.algorand.android.ui.asset.selection.view.model.BaseSelectAssetItem.SelectCollectibleItem.CollectibleType
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.orZero
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.model.AssetLite.Type
import com.algorand.wallet.asset.domain.model.CollectibleMediaType
import javax.inject.Inject

class AssetSelectionMapper @Inject constructor(
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val assetItemConfigurationMapper: AssetItemConfigurationMapper,
) {

    fun createAssetSelectionItems(assetLites: PagingData<AssetLite>): PagingData<BaseSelectAssetItem> {
        return assetLites.map { assetLite ->
            when (val assetLiteType = assetLite.type) {
                is Type.Asset -> createAssetSelectionItems(assetLite)
                is Type.Collectible -> mapToSelectCollectibleItem(assetLite, assetLiteType)
            }
        }
    }

    private suspend fun createAssetSelectionItems(assetLite: AssetLite): BaseSelectAssetItem {
        val parityValueInSelectedCurrency = assetLite.getParityValueInSelectedCurrency()
        val assetItemConfig = assetItemConfigurationMapper.mapTo(
            isAmountInSelectedCurrencyVisible = assetLite.usdValue != null,
            secondaryValueText = parityValueInSelectedCurrency.getFormattedValue(isCompact = true),
            formattedCompactAmount = parityValueInSelectedCurrency.getFormattedCompactValue(),
            assetId = assetLite.assetId,
            name = assetLite.name,
            shortName = assetLite.shortName,
            verificationTier = assetLite.verificationTier,
            primaryValue = parityValueInSelectedCurrency.amountAsCurrency
        )
        return SelectAssetItem(assetItemConfig)
    }

    private fun mapToSelectCollectibleItem(assetLite: AssetLite, collectible: Type.Collectible): SelectCollectibleItem {
        val collectibleType = when (collectible.mediaType) {
            CollectibleMediaType.IMAGE -> CollectibleType.Image
            CollectibleMediaType.VIDEO -> CollectibleType.Video
            CollectibleMediaType.MIXED -> CollectibleType.Mixed
            CollectibleMediaType.AUDIO -> CollectibleType.Audio
            CollectibleMediaType.UNKNOWN -> CollectibleType.NotSupported
        }
        return mapToSelectCollectibleItem(assetLite, collectibleType)
    }

    private fun mapToSelectCollectibleItem(assetLite: AssetLite, type: CollectibleType): SelectCollectibleItem {
        val parityValueInSelectedCurrency = assetLite.getParityValueInSelectedCurrency()
        return SelectCollectibleItem(
            id = assetLite.assetId,
            isAlgo = assetLite.isAlgo,
            shortName = assetLite.shortName,
            name = assetLite.name,
            amount = assetLite.amount,
            formattedAmount = assetLite.getFormattedAmount(),
            formattedCompactAmount = assetLite.getFormattedCompactAmount(),
            formattedSelectedCurrencyValue = parityValueInSelectedCurrency.getFormattedValue(),
            formattedSelectedCurrencyCompactValue = parityValueInSelectedCurrency.getFormattedCompactValue(),
            isAmountInSelectedCurrencyVisible = assetLite.isAmountInSelectedCurrencyVisible(),
            avatarDisplayText = AssetName.create(assetLite.name),
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetLite),
            optedInAtRound = assetLite.optedInAtRound,
            amountInSelectedCurrency = parityValueInSelectedCurrency.amountAsCurrency,
            type = type
        )
    }

    private fun AssetLite.getParityValueInSelectedCurrency(): ParityValue {
        return getPrimaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
    }
}

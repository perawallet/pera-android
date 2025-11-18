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

package com.algorand.android.modules.accountdetail.assets.ui.mapper

import com.algorand.android.R
import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.modules.accountdetail.assets.ui.decider.NFTIndicatorDrawableDecider
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAccountsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.BaseAssetItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.BaseAssetItem.BaseOwnedItem.AssetItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.BaseAssetItem.BasePendingItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.BaseAssetItem.BasePendingItem.NFTItem
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.modules.collectibles.util.deciders.NFTAmountFormatDecider
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.AmountRenderer.RenderType
import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.formatting.FormatAmountByCollectibleFractionalDigit
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.model.AssetLite.Type
import com.algorand.wallet.utils.orZero
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

// TODO Rename this function to make it screen independent
class AccountDetailAssetItemMapper @Inject constructor(
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val nftIndicatorDrawableDecider: NFTIndicatorDrawableDecider,
    private val nftAmountFormatDecider: NFTAmountFormatDecider,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
    private val formatAmountByCollectibleFractionalDigit: FormatAmountByCollectibleFractionalDigit,
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo
) {

    fun mapToAssetListItem(
        assetLite: AssetLite,
        isHoldingByWatchAccount: Boolean,
        amountRendererType: RenderType
    ): AccountDetailAssetsItem {
        return when (assetLite.type) {
            is Type.Asset -> mapToAssetListItem(assetLite, amountRendererType)
            is Type.Collectible -> mapToCollectibleListItem(assetLite, isHoldingByWatchAccount, amountRendererType)
        }
    }

    private fun mapToAssetListItem(assetLite: AssetLite, amountRendererType: RenderType): AccountDetailAssetsItem {
        return when (assetLite.assetStatus) {
            AssetStatus.OWNED_BY_ACCOUNT -> mapToOwnedAssetItem(assetLite, amountRendererType)
            AssetStatus.PENDING_FOR_ADDITION -> mapToPendingAdditionAssetItem(assetLite)
            AssetStatus.PENDING_FOR_REMOVAL -> mapToPendingRemovalAssetItem(assetLite)
        }
    }

    private fun mapToCollectibleListItem(
        assetLite: AssetLite,
        isHoldingByWatchAccount: Boolean,
        amountRendererType: RenderType
    ): AccountDetailAssetsItem {
        return when (assetLite.assetStatus) {
            AssetStatus.OWNED_BY_ACCOUNT -> {
                val isOwned = assetLite.amount isGreaterThan BigInteger.ZERO
                val isAmountVisible = assetLite.amount isGreaterThan BigInteger.ONE
                mapToOwnedNFTItem(
                    assetLite,
                    isHoldingByWatchAccount,
                    isOwned,
                    isAmountVisible,
                    shouldDecreaseOpacity = !isOwned || isHoldingByWatchAccount,
                    amountRendererType
                )
            }

            AssetStatus.PENDING_FOR_ADDITION -> mapToPendingAdditionNFTITem(assetLite)
            AssetStatus.PENDING_FOR_REMOVAL -> mapToPendingRemovalNFTItem(assetLite)
        }
    }

    private fun mapToOwnedAssetItem(assetLite: AssetLite, amountRendererType: RenderType): AssetItem {
        return with(assetLite) {
            val primaryParityValue = getAssetItemPrimaryParityValue(assetLite)
            val currencyAmountRenderer = getSelectedCurrencyRenderer(primaryParityValue, amountRendererType)
            AssetItem(
                id = assetId,
                name = AssetName.create(name),
                shortName = AssetName.createShortName(shortName),
                formattedAmount = getAmountRenderer(assetLite, amountRendererType).getDisplayValue(),
                formattedDisplayedCurrencyValue = currencyAmountRenderer.getDisplayValue(),
                isAmountInDisplayedCurrencyVisible = usdValue != null && usdValue.orZero() > BigDecimal.ZERO,
                verificationTierConfiguration = verificationTierConfigurationDecider
                    .decideVerificationTierConfiguration(verificationTier),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                amountInSelectedCurrency = primaryParityValue.amountAsCurrency,
                isFavorite = assetLite.isFavorite
            )
        }
    }

    private fun getAssetItemPrimaryParityValue(assetLite: AssetLite): ParityValue {
        return with(assetLite) {
            if (isAlgo && isPrimaryCurrencyAlgo()) {
                getSecondaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
            } else {
                getPrimaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
            }
        }
    }

    private fun getSelectedCurrencyRenderer(parityValue: ParityValue, amountRendererType: RenderType): AmountRenderer {
        val currencyAmount = PeraAmount(parityValue.amountAsCurrency)
        val formattedAmount = SimplePlainFormattedAmount(currencyAmount, DecimalConfig(2))
        return AmountRenderer(formattedAmount, amountRendererType, parityValue.selectedCurrencySymbol)
    }

    private fun getAmountRenderer(assetLite: AssetLite, amountRendererType: RenderType): AmountRenderer {
        return with(assetLite) {
            val assetAmount = SimplePlainFormattedAmount(PeraAmount(amount, decimal), DecimalConfig(decimal))
            AmountRenderer(assetAmount, amountRendererType)
        }
    }

    private fun mapToPendingAdditionAssetItem(assetLite: AssetLite): BasePendingItem.AssetItem.AdditionItem {
        return BasePendingItem.AssetItem.AdditionItem(
            id = assetLite.assetId,
            name = AssetName.create(assetLite.name),
            shortName = AssetName.createShortName(assetLite.shortName),
            actionDescriptionResId = R.string.adding_asset,
            verificationTierConfiguration = verificationTierConfigurationDecider.decideVerificationTierConfiguration(
                assetLite.verificationTier
            ),
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetLite)
        )
    }

    private fun mapToPendingRemovalAssetItem(assetLite: AssetLite): BasePendingItem.AssetItem.RemovalItem {
        return BasePendingItem.AssetItem.RemovalItem(
            id = assetLite.assetId,
            name = AssetName.create(assetLite.name),
            shortName = AssetName.createShortName(assetLite.shortName),
            actionDescriptionResId = R.string.removing_asset,
            verificationTierConfiguration = verificationTierConfigurationDecider.decideVerificationTierConfiguration(
                assetLite.verificationTier
            ),
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetLite)
        )
    }

    fun mapToSearchViewItem(query: String): AccountDetailAccountsItem.SearchViewItem {
        return AccountDetailAccountsItem.SearchViewItem(query = query)
    }

    fun mapToBackupWarningItem(
        isBackedUp: Boolean
    ): AccountDetailAccountsItem.BackupWarningItem {
        return AccountDetailAccountsItem.BackupWarningItem(isBackedUp)
    }

    fun mapToTitleItem(titleRes: Int, isAddAssetButtonVisible: Boolean): AccountDetailAccountsItem.TitleItem {
        return AccountDetailAccountsItem.TitleItem(titleRes, isAddAssetButtonVisible)
    }

    fun mapToNoAssetFoundViewItem(): AccountDetailAssetsItem.NoAssetFoundViewItem {
        return AccountDetailAssetsItem.NoAssetFoundViewItem
    }

    private fun mapToOwnedNFTItem(
        assetLite: AssetLite,
        isHoldingByWatchAccount: Boolean,
        isOwned: Boolean,
        isAmountVisible: Boolean,
        shouldDecreaseOpacity: Boolean,
        amountRendererType: RenderType
    ): BaseAssetItem.BaseOwnedItem.NFTItem {
        return with(assetLite) {
            val formattedAmount = nftAmountFormatDecider.decideNFTAmountFormat(
                nftAmount = amount,
                fractionalDecimal = decimal,
                formattedAmount = formatAmountByCollectibleFractionalDigit(amount, decimal),
                formattedCompactAmount = formatAmountByCollectibleFractionalDigit(amount, decimal, true)
            )
            val amountRenderer = AmountRenderer(SimpleFormattedAmount(formattedAmount), amountRendererType)
            BaseAssetItem.BaseOwnedItem.NFTItem(
                id = assetId,
                name = AssetName.create(name),
                shortName = AssetName.createShortName(shortName),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                formattedAmount = amountRenderer.getDisplayValue(),
                nftIndicatorDrawable = nftIndicatorDrawableDecider.decideNFTIndicatorDrawable(
                    isOwned = isOwned,
                    isHoldingByWatchAccount = isHoldingByWatchAccount,
                    nftListingViewType = NFTListingViewType.LINEAR_VERTICAL
                ),
                shouldDecreaseOpacity = shouldDecreaseOpacity,
                isAmountVisible = isAmountVisible,
                collectionName = (assetLite.type as? Type.Collectible)?.collectionName,
                isFavorite = assetLite.isFavorite
            )
        }
    }

    private fun mapToPendingAdditionNFTITem(assetLite: AssetLite): NFTItem.AdditionItem {
        return NFTItem.AdditionItem(
            id = assetLite.assetId,
            name = AssetName.create(assetLite.name),
            shortName = AssetName.createShortName(assetLite.shortName),
            actionDescriptionResId = R.string.adding_asset,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetLite),
            collectionName = (assetLite.type as? Type.Collectible)?.collectionName
        )
    }

    private fun mapToPendingRemovalNFTItem(assetLite: AssetLite): NFTItem.RemovalItem {
        return NFTItem.RemovalItem(
            id = assetLite.assetId,
            name = AssetName.create(assetLite.name),
            shortName = AssetName.createShortName(assetLite.shortName),
            actionDescriptionResId = R.string.removing_asset,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetLite),
            collectionName = (assetLite.type as? Type.Collectible)?.collectionName
        )
    }
}

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
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.BaseAssetItem.BasePendingItem
import com.algorand.android.modules.accountdetail.assets.ui.model.AccountDetailAssetsItem.BaseAssetItem.BasePendingItem.NFTItem
import com.algorand.android.modules.accountdetail.assets.ui.model.QuickActionItem
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.modules.collectibles.util.deciders.NFTAmountFormatDecider
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.formatting.FormatAmountByCollectibleFractionalDigit
import com.algorand.android.utils.isGreaterThan
import com.algorand.android.utils.orZero
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.model.AssetLite.Type
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
    private val formatAmountByCollectibleFractionalDigit: FormatAmountByCollectibleFractionalDigit
) {

    fun mapToAssetListItem(assetLite: AssetLite, isHoldingByWatchAccount: Boolean): AccountDetailAssetsItem {
        return when (assetLite.type) {
            is Type.Asset -> mapToAssetListItem(assetLite)
            is Type.Collectible -> mapToCollectibleListItem(assetLite, isHoldingByWatchAccount)
        }
    }

    private fun mapToAssetListItem(assetLite: AssetLite): AccountDetailAssetsItem {
        return when (assetLite.assetStatus) {
            AssetStatus.OWNED_BY_ACCOUNT -> mapToOwnedAssetItem(assetLite)
            AssetStatus.PENDING_FOR_ADDITION -> mapToPendingAdditionAssetItem(assetLite)
            AssetStatus.PENDING_FOR_REMOVAL -> mapToPendingRemovalAssetItem(assetLite)
        }
    }

    private fun mapToCollectibleListItem(
        assetLite: AssetLite,
        isHoldingByWatchAccount: Boolean
    ): AccountDetailAssetsItem {
        return when (assetLite.assetStatus) {
            AssetStatus.OWNED_BY_ACCOUNT -> {
                val isOwned = assetLite.amount isGreaterThan BigInteger.ZERO
                val isAmountVisible = assetLite.amount isGreaterThan BigInteger.ONE
                mapToOwnedNFTItem(
                    assetLite = assetLite,
                    isHoldingByWatchAccount = isHoldingByWatchAccount,
                    isOwned = isOwned,
                    isAmountVisible = isAmountVisible,
                    shouldDecreaseOpacity = !isOwned || isHoldingByWatchAccount
                )
            }

            AssetStatus.PENDING_FOR_ADDITION -> mapToPendingAdditionNFTITem(assetLite)
            AssetStatus.PENDING_FOR_REMOVAL -> mapToPendingRemovalNFTItem(assetLite)
        }
    }

    private fun mapToOwnedAssetItem(assetLite: AssetLite): BaseAssetItem.BaseOwnedItem.AssetItem {
        return with(assetLite) {
            val primaryParityValue = getPrimaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
            BaseAssetItem.BaseOwnedItem.AssetItem(
                id = assetId,
                name = AssetName.create(name),
                shortName = AssetName.createShortName(shortName),
                formattedAmount = amount.formatAmount(decimal, isCompact = true),
                formattedDisplayedCurrencyValue = primaryParityValue.getFormattedCompactValue(),
                isAmountInDisplayedCurrencyVisible = usdValue != null && usdValue.orZero() > BigDecimal.ZERO,
                verificationTierConfiguration = verificationTierConfigurationDecider
                    .decideVerificationTierConfiguration(verificationTier),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                amountInSelectedCurrency = primaryParityValue.amountAsCurrency
            )
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

    fun mapToSwapQuickActionItem(isSelected: Boolean): QuickActionItem.SwapButton {
        return QuickActionItem.SwapButton(isSelected)
    }

    fun mapToQuickActionItemContainer(
        quickActionItemList: List<QuickActionItem>
    ): AccountDetailAccountsItem.QuickActionItemContainer {
        return AccountDetailAccountsItem.QuickActionItemContainer(quickActionItemList)
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

    fun mapToRequiredMinimumBalanceItem(
        formattedRequiredMinimumBalance: String
    ): AccountDetailAccountsItem.RequiredMinimumBalanceItem {
        return AccountDetailAccountsItem.RequiredMinimumBalanceItem(
            formattedRequiredMinimumBalance = formattedRequiredMinimumBalance
        )
    }

    private fun mapToOwnedNFTItem(
        assetLite: AssetLite,
        isHoldingByWatchAccount: Boolean,
        isOwned: Boolean,
        isAmountVisible: Boolean,
        shouldDecreaseOpacity: Boolean
    ): BaseAssetItem.BaseOwnedItem.NFTItem {
        return with(assetLite) {
            BaseAssetItem.BaseOwnedItem.NFTItem(
                id = assetId,
                name = AssetName.create(name),
                shortName = AssetName.createShortName(shortName),
                baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(this),
                formattedAmount = nftAmountFormatDecider.decideNFTAmountFormat(
                    nftAmount = amount,
                    fractionalDecimal = decimal,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(amount, decimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(amount, decimal, true)
                ),
                nftIndicatorDrawable = nftIndicatorDrawableDecider.decideNFTIndicatorDrawable(
                    isOwned = isOwned,
                    isHoldingByWatchAccount = isHoldingByWatchAccount,
                    nftListingViewType = NFTListingViewType.LINEAR_VERTICAL
                ),
                shouldDecreaseOpacity = shouldDecreaseOpacity,
                isAmountVisible = isAmountVisible,
                collectionName = (assetLite.type as? Type.Collectible)?.collectionName
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

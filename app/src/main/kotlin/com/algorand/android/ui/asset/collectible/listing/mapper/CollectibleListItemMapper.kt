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

package com.algorand.android.ui.asset.collectible.listing.mapper

import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.modules.accountdetail.assets.ui.decider.NFTIndicatorDrawableDecider
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.modules.collectibles.util.deciders.NFTAmountFormatDecider
import com.algorand.android.nft.domain.decider.BaseCollectibleListItemItemTypeDecider
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.model.AssetStatus
import com.algorand.wallet.asset.domain.model.AssetLite
import java.math.BigInteger
import javax.inject.Inject

class CollectibleListItemMapper @Inject constructor(
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val baseCollectibleListItemItemTypeDecider: BaseCollectibleListItemItemTypeDecider,
    private val nftIndicatorDrawableDecider: NFTIndicatorDrawableDecider,
    private val nftAmountFormatDecider: NFTAmountFormatDecider
) {

    fun createCollectibleListItem(
        assetLite: AssetLite,
        ownerAccountLite: AccountLite?,
        nftListingType: NFTListingViewType
    ): CollectibleListItem {
        return when (assetLite.assetStatus) {
            AssetStatus.OWNED_BY_ACCOUNT -> {
                createOwnedCollectibleListItem(
                    assetLite = assetLite,
                    nftListingType = nftListingType,
                    isOwnedByWatchAccount = ownerAccountLite?.cachedInfo?.type == AccountType.NoAuth
                )
            }
            AssetStatus.PENDING_FOR_REMOVAL, AssetStatus.PENDING_FOR_ADDITION -> {
                mapToPendingCollectibleListItem(
                    assetLite = assetLite,
                    nftListingViewType = nftListingType
                )
            }
        }
    }

    private fun createOwnedCollectibleListItem(
        assetLite: AssetLite,
        nftListingType: NFTListingViewType,
        isOwnedByWatchAccount: Boolean
    ): CollectibleListItem {
        with(assetLite) {
            val isAmountVisible = assetLite.amount > BigInteger.ONE
            return mapToOwnedCollectibleListItem(
                assetLite = this,
                isAmountVisible = isAmountVisible,
                nftListingViewType = nftListingType,
                isOptedIn = assetLite.amount.isGreaterThan(BigInteger.ZERO),
                isOwnedByWatchAccount = isOwnedByWatchAccount
            )
        }
    }

    private fun mapToOwnedCollectibleListItem(
        assetLite: AssetLite,
        isAmountVisible: Boolean,
        nftListingViewType: NFTListingViewType,
        isOptedIn: Boolean,
        isOwnedByWatchAccount: Boolean
    ): CollectibleListItem {
        val collectibleDetail = assetLite.type as AssetLite.Type.Collectible
        return CollectibleListItem(
            collectibleId = assetLite.assetId,
            collectibleName = AssetName.create(collectibleDetail.name),
            collectionName = collectibleDetail.collectionName,
            optedInAccountAddress = assetLite.address,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetLite),
            itemType = baseCollectibleListItemItemTypeDecider.decideSimpleNFTViewType(nftListingViewType),
            type = CollectibleListItem.CollectibleType.Owned(
                formattedCollectibleAmount = nftAmountFormatDecider.decideNFTAmountFormat(
                    nftAmount = assetLite.amount,
                    fractionalDecimal = assetLite.decimal,
                    formattedAmount = assetLite.amount.formatAmount(assetLite.decimal),
                    formattedCompactAmount = assetLite.amount.formatAmount(assetLite.decimal, isCompact = true)
                ),
                shouldDecreaseOpacity = !isOptedIn,
                isAmountVisible = isAmountVisible,
                nftIndicatorDrawable = nftIndicatorDrawableDecider.decideNFTIndicatorDrawable(
                    isOwned = isOptedIn,
                    isHoldingByWatchAccount = isOwnedByWatchAccount,
                    nftListingViewType = nftListingViewType
                )
            )
        )
    }

    private fun mapToPendingCollectibleListItem(
        assetLite: AssetLite,
        nftListingViewType: NFTListingViewType
    ): CollectibleListItem {
        val collectibleDetail = assetLite.type as AssetLite.Type.Collectible
        return CollectibleListItem(
            collectibleId = assetLite.assetId,
            collectibleName = AssetName.create(collectibleDetail.name),
            collectionName = collectibleDetail.collectionName,
            optedInAccountAddress = assetLite.address,
            baseAssetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetLite),
            itemType = baseCollectibleListItemItemTypeDecider.decideSimplePendingNFTViewType(nftListingViewType),
            type = CollectibleListItem.CollectibleType.Pending
        )
    }
}

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

package com.algorand.android.modules.collectibles.detail.ui.mapper

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.assets.core.ui.domain.model.AssetName
import com.algorand.android.modules.collectibles.detail.base.domain.decider.CollectibleDetailDecider
import com.algorand.android.modules.collectibles.detail.base.ui.mapper.CollectibleMediaItemMapper
import com.algorand.android.modules.collectibles.detail.base.ui.mapper.CollectibleTraitItemMapper
import com.algorand.android.modules.collectibles.detail.base.ui.model.BaseCollectibleMediaItem
import com.algorand.android.modules.collectibles.detail.base.ui.model.CollectibleTraitItem
import com.algorand.android.modules.collectibles.detail.ui.model.NFTDetailPreview
import com.algorand.android.modules.collectibles.util.deciders.NFTAmountFormatDecider
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import javax.inject.Inject

class NFTDetailPreviewMapper @Inject constructor(
    private val collectibleMediaItemMapper: CollectibleMediaItemMapper,
    private val collectibleTraitItemMapper: CollectibleTraitItemMapper,
    private val collectibleDetailDecider: CollectibleDetailDecider,
    private val nftAmountFormatDecider: NFTAmountFormatDecider,
) {

    fun mapToNFTDetailPreview(
        ownedCollectibleData: BaseOwnedCollectibleData?,
        collectibleDetail: CollectibleDetail,
        nftName: AssetName,
        optedInAccountTypeDrawableResId: Int,
        optedInAccountDisplayName: AccountDisplayName,
        creatorAccountOfNFT: AccountDisplayName,
        accountType: AccountType?,
        isOwnerActionsGroupVisible: Boolean,
        isOptOutButtonVisible: Boolean,
    ): NFTDetailPreview {
        val mediaList = mapToMediaList(collectibleDetail)
        return NFTDetailPreview(
            nftName = nftName,
            collectionNameOfNFT = collectibleDetail.collectionName,
            optedInAccountTypeDrawableResId = optedInAccountTypeDrawableResId,
            optedInAccountDisplayName = optedInAccountDisplayName,
            formattedNFTAmount = mapToFormattedCollectibleAmount(ownedCollectibleData),
            mediaListOfNFT = mediaList,
            traitListOfNFT = mapToTraitList(collectibleDetail),
            nftDescription = collectibleDetail.collectibleInfo.collectibleDescription,
            creatorAccountAddressOfNFT = creatorAccountOfNFT,
            nftId = collectibleDetail.id,
            formattedTotalSupply = mapToFormattedTotalSupply(collectibleDetail),
            peraExplorerUrl = collectibleDetail.assetInfo?.explorerUrl.orEmpty(),
            isPureNFT = collectibleDetail.isPure,
            primaryWarningResId = collectibleDetailDecider.decideWarningTextRes(collectibleDetail.prismUrl),
            secondaryWarningResId = getSecondaryWarningResId(ownedCollectibleData, accountType),
            globalErrorEvent = null,
            nftSendEvent = null,
            isOptOutButtonVisible = isOptOutButtonVisible,
            isOwnerActionsGroupVisible = isOwnerActionsGroupVisible,
            isCopyEnabled = isMediaCopiable(mediaList.firstOrNull()?.itemType) && isOwnerActionsGroupVisible,
        )
    }

    private fun getSecondaryWarningResId(
        ownedCollectibleData: BaseOwnedCollectibleData?,
        accountType: AccountType?
    ): Int? {
        return collectibleDetailDecider.decideOptedInWarningTextRes(
            isOwnedByTheUser = ownedCollectibleData?.isOwnedByTheUser ?: false,
            accountType = accountType
        )
    }

    private fun mapToFormattedTotalSupply(collectibleDetail: CollectibleDetail): String {
        return nftAmountFormatDecider.decideNFTAmountFormat(
            nftAmount = collectibleDetail.assetInfo?.supply?.total,
            fractionalDecimal = collectibleDetail.assetInfo?.decimals
        )
    }

    private fun mapToFormattedCollectibleAmount(ownedCollectibleData: BaseOwnedCollectibleData?): String {
        return nftAmountFormatDecider.decideNFTAmountFormat(
            nftAmount = ownedCollectibleData?.amount,
            fractionalDecimal = ownedCollectibleData?.decimals,
            formattedAmount = ownedCollectibleData?.formattedAmount,
            formattedCompactAmount = ownedCollectibleData?.formattedCompactAmount
        )
    }

    private fun mapToTraitList(collectibleDetail: CollectibleDetail): List<CollectibleTraitItem> {
        return collectibleDetail.collectibleInfo.traits?.mapNotNull {
            collectibleTraitItemMapper.mapToTraitItem(it)
        }.orEmpty()
    }

    private fun isMediaCopiable(mediaType: BaseCollectibleMediaItem.ItemType?): Boolean {
        return mediaType == BaseCollectibleMediaItem.ItemType.IMAGE
    }

    private fun mapToMediaList(collectibleDetail: CollectibleDetail): List<BaseCollectibleMediaItem> {
        return collectibleDetail.collectibleMedias.map {
            collectibleMediaItemMapper.mapToCollectibleMediaItem(
                baseCollectibleMedia = it,
                shouldDecreaseOpacity = false,
                collectibleDetail = collectibleDetail,
                showMediaButtons = true
            )
        }
    }
}

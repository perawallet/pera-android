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

package com.algorand.android.modules.collectibles.profile.ui.mapper

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.assets.core.ui.domain.model.AssetName
import com.algorand.android.modules.assets.profile.asaprofile.ui.model.AsaStatusPreview
import com.algorand.android.modules.collectibles.detail.base.domain.decider.CollectibleDetailDecider
import com.algorand.android.modules.collectibles.detail.base.ui.mapper.CollectibleMediaItemMapper
import com.algorand.android.modules.collectibles.detail.base.ui.mapper.CollectibleTraitItemMapper
import com.algorand.android.modules.collectibles.detail.base.ui.model.BaseCollectibleMediaItem
import com.algorand.android.modules.collectibles.detail.base.ui.model.CollectibleTraitItem
import com.algorand.android.modules.collectibles.profile.ui.model.CollectibleProfilePreview
import com.algorand.android.modules.collectibles.util.deciders.NFTAmountFormatDecider
import com.algorand.wallet.asset.domain.model.CollectibleDetail
import javax.inject.Inject

class CollectibleProfilePreviewMapper @Inject constructor(
    private val collectibleMediaItemMapper: CollectibleMediaItemMapper,
    private val nftAmountFormatDecider: NFTAmountFormatDecider,
    private val collectibleTraitItemMapper: CollectibleTraitItemMapper,
    private val collectibleDetailDecider: CollectibleDetailDecider,
) {

    fun mapToCollectibleProfilePreview(
        collectibleDetail: CollectibleDetail,
        asaStatusPreview: AsaStatusPreview?,
        nftName: AssetName,
        creatorAccountAddressOfNFT: AccountDisplayName,
        accountAddress: String,
        isOptedInByAccount: Boolean
    ): CollectibleProfilePreview {
        return CollectibleProfilePreview(
            nftName = nftName,
            collectionNameOfNFT = collectibleDetail.collectionName,
            mediaListOfNFT = mapToMediaList(collectibleDetail, isOptedInByAccount),
            traitListOfNFT = mapToTraitList(collectibleDetail),
            nftDescription = collectibleDetail.collectibleInfo.collectibleDescription,
            creatorAccountAddressOfNFT = creatorAccountAddressOfNFT,
            nftId = collectibleDetail.id,
            formattedTotalSupply = mapToFormattedTotalSupply(collectibleDetail),
            peraExplorerUrl = collectibleDetail.assetInfo?.explorerUrl.orEmpty(),
            isPureNFT = collectibleDetail.isPure,
            primaryWarningResId = collectibleDetailDecider.decideWarningTextRes(collectibleDetail.prismUrl),
            secondaryWarningResId = null,
            collectibleStatusPreview = asaStatusPreview,
            accountAddress = accountAddress
        )
    }

    private fun mapToMediaList(
        collectibleDetail: CollectibleDetail,
        isOptedInByAccount: Boolean
    ): List<BaseCollectibleMediaItem> {
        return collectibleDetail.collectibleMedias.map { nftMedia ->
            collectibleMediaItemMapper.mapToCollectibleMediaItem(
                baseCollectibleMedia = nftMedia,
                shouldDecreaseOpacity = !isOptedInByAccount,
                collectibleDetail = collectibleDetail,
                showMediaButtons = true
            )
        }
    }

    private fun mapToTraitList(collectibleDetail: CollectibleDetail): List<CollectibleTraitItem>? {
        return collectibleDetail.collectibleInfo.traits?.mapNotNull {
            collectibleTraitItemMapper.mapToTraitItem(it)
        }
    }

    private fun mapToFormattedTotalSupply(collectibleDetail: CollectibleDetail): String {
        return nftAmountFormatDecider.decideNFTAmountFormat(
            nftAmount = collectibleDetail.assetInfo?.supply?.total,
            fractionalDecimal = collectibleDetail.assetInfo?.decimals
        )
    }
}

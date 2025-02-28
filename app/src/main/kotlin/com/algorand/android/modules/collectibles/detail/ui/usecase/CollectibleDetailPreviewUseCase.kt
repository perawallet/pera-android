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

package com.algorand.android.modules.collectibles.detail.ui.usecase

import com.algorand.android.modules.accountcore.domain.usecase.GetAccountOwnedCollectibleData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.assets.core.ui.domain.usecase.GetAssetName
import com.algorand.android.modules.collectibles.detail.ui.mapper.NFTDetailPreviewMapper
import com.algorand.android.modules.collectibles.detail.ui.model.NFTDetailPreview
import com.algorand.android.utils.Event
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.asset.domain.usecase.FetchCollectibleDetail
import javax.inject.Inject

class CollectibleDetailPreviewUseCase @Inject constructor(
    private val nftDetailPreviewMapper: NFTDetailPreviewMapper,
    private val fetchCollectibleDetail: FetchCollectibleDetail,
    private val getAssetName: GetAssetName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountType: GetAccountType,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountOwnedCollectibleData: GetAccountOwnedCollectibleData
) {

    fun getSendEventPreviewAccordingToNFTType(preview: NFTDetailPreview?): NFTDetailPreview? {
        return preview?.copy(nftSendEvent = Event(Unit))
    }

    suspend fun getCollectibleDetailPreview(nftId: Long, accountAddress: String): NFTDetailPreview? {
        return fetchCollectibleDetail(nftId).map { collectibleDetail ->
            val accountType = getAccountType(accountAddress)
            val ownedCollectibleData = getAccountOwnedCollectibleData(accountAddress, nftId)
            val isOwnedByTheUser = ownedCollectibleData?.isOwnedByTheUser ?: false
            val isCreatedByOwnerAccount = collectibleDetail.creatorAddress == accountAddress
            val isOwnedByWatchAccount = accountType is AccountType.NoAuth
            nftDetailPreviewMapper.mapToNFTDetailPreview(
                ownedCollectibleData = ownedCollectibleData,
                collectibleDetail = collectibleDetail,
                nftName = getAssetName(collectibleDetail.title ?: collectibleDetail.fullName.orEmpty()),
                optedInAccountTypeDrawableResId = getAccountIconDrawablePreview(accountAddress).iconResId,
                optedInAccountDisplayName = getAccountDisplayName(accountAddress),
                creatorAccountOfNFT = getAccountDisplayName(collectibleDetail.creatorAddress.orEmpty()),
                isOwnerActionsGroupVisible = isOwnedByTheUser && !isOwnedByWatchAccount,
                isOptOutButtonVisible = !isOwnedByTheUser && !isCreatedByOwnerAccount && !isOwnedByWatchAccount,
                accountType = accountType
            )
        }.getDataOrNull()
    }
}

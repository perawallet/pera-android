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

package com.algorand.android.nft.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.GetCollectibleDetail
import com.algorand.android.module.contacts.domain.usecase.GetContactByAddress
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.nft.mapper.CollectibleTransactionApprovePreviewMapper
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.toShortenedAddress
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class CollectibleTransactionApprovePreviewUseCase @Inject constructor(
    private val collectibleTransactionApprovePreviewMapper: CollectibleTransactionApprovePreviewMapper,
    private val getAccountDetail: GetAccountDetail,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getContactByAddress: GetContactByAddress,
    private val getLocalAccounts: GetLocalAccounts,
    private val getCollectibleDetail: GetCollectibleDetail
) {

    fun getCollectibleTransactionApprovePreviewFlow(
        nftId: Long,
        senderPublicKey: String,
        receiverPublicKey: String,
        fee: Float,
        nftDomainName: String?,
        nftDomainLogoUrl: String?
    ) = flow {
        val (receiverDisplayText, receiverAccountIcon) = getAccountOrContactDisplayTextAndIcon(receiverPublicKey)
        val ownerAccountDetail = getAccountDetail(senderPublicKey)
        val isHoldingByWatchAccount = ownerAccountDetail.accountType == AccountType.NoAuth
        val isOwnedByTheUser = isAssetOwnedByAccount(senderPublicKey, nftId)
        val nftDetail = getCollectibleDetail(nftId)
        val isOptOutGroupVisible = isOwnedByTheUser &&
            !isHoldingByWatchAccount &&
            nftDetail?.creatorAddress != ownerAccountDetail.address &&
            senderPublicKey != receiverPublicKey

        val collectibleTransactionApprovePreview = collectibleTransactionApprovePreviewMapper.mapToPreview(
            senderAccountPublicKey = senderPublicKey,
            senderAccountDisplayText = getAccountDisplayName(senderPublicKey).primaryDisplayName,
            senderAccountIconResource = getAccountIconDrawablePreview(senderPublicKey),
            receiverAccountPublicKey = receiverPublicKey,
            receiverAccountDisplayText = receiverDisplayText,
            receiverAccountIconDrawablePreview = receiverAccountIcon,
            formattedTransactionFee = fee.toLong().formatAsAlgoString().formatAsAlgoAmount(),
            isOptOutGroupVisible = isOptOutGroupVisible,
            nftDomainName = nftDomainName,
            nftDomainLogoUrl = nftDomainLogoUrl
        )
        emit(collectibleTransactionApprovePreview)
    }

    private suspend fun getAccountOrContactDisplayTextAndIcon(
        accountAddress: String
    ): Pair<String, AccountIconDrawablePreview?> {
        val localReceiver = getLocalAccounts().firstOrNull { it.address == accountAddress }
        if (localReceiver != null) {
            val accountName = getAccountDisplayName(accountAddress).primaryDisplayName
            val accountIcon = getAccountIconDrawablePreview(accountAddress)
            return accountName to accountIcon
        }
        val contactReceiver = getContactByAddress(accountAddress)
        return (contactReceiver?.name ?: accountAddress.toShortenedAddress()) to null
    }
}

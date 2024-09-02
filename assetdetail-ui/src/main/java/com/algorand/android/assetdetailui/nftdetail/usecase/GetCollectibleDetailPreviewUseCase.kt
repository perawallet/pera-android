/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.assetdetailui.nftdetail.usecase

import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconResourceByAccountType
import com.algorand.android.accountcore.ui.usecase.GetAssetName
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.assetdetail.component.collectible.domain.usecase.FetchCollectibleDetail
import com.algorand.android.assetdetailui.detail.nftprofile.mapper.CollectibleAmountFormatter
import com.algorand.android.assetdetailui.detail.nftprofile.mapper.CollectibleDetailWarningTextMapper
import com.algorand.android.assetdetailui.detail.nftprofile.mapper.CollectibleMediaItemMapper
import com.algorand.android.assetdetailui.detail.nftprofile.mapper.CollectibleTraitItemMapper
import com.algorand.android.assetdetailui.detail.nftprofile.model.BaseCollectibleMediaItem
import com.algorand.android.assetdetailui.detail.nftprofile.model.CollectibleMediaItemMapperPayload
import com.algorand.android.assetdetailui.nftdetail.model.CollectibleDetailPreview
import com.algorand.android.core.component.collectible.domain.usecase.GetAccountCollectibleDetail
import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.foundation.common.isGreaterThan
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetCollectibleDetailPreviewUseCase @Inject constructor(
    private val fetchCollectibleDetail: FetchCollectibleDetail,
    private val getAssetName: GetAssetName,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconResourceByAccountType: GetAccountIconResourceByAccountType,
    private val getAccountDetail: GetAccountDetail,
    private val collectibleAmountFormatter: CollectibleAmountFormatter,
    private val getAccountCollectibleDetail: GetAccountCollectibleDetail,
    private val collectibleMediaItemMapper: CollectibleMediaItemMapper,
    private val collectibleTraitItemMapper: CollectibleTraitItemMapper,
    private val collectibleDetailWarningTextMapper: CollectibleDetailWarningTextMapper
) : GetCollectibleDetailPreview {

    override suspend fun invoke(nftId: Long, accountAddress: String): Flow<CollectibleDetailPreview?> = flow {
        // TODO LOADING STATE -> return flow
        fetchCollectibleDetail(nftId).use(
            onSuccess = {
                emit(createPreview(it, accountAddress))
            },
            onFailed = { _, _ ->
                // TODO error state
            }
        )
    }

    private suspend fun createPreview(
        collectibleDetail: CollectibleDetail,
        accountAddress: String
    ): CollectibleDetailPreview {
        val accountDetail = getAccountDetail(accountAddress)
        val accountCollectibleDetail = getAccountCollectibleDetail(accountAddress, collectibleDetail)
        val hasAmount = accountCollectibleDetail?.amount?.isGreaterThan(BigInteger.ZERO) == true
        val isOwnedByTheUser = accountCollectibleDetail != null && hasAmount
        val isOwnedByWatchAccount = accountDetail.accountType == AccountType.NoAuth
        return with(collectibleDetail) {
            val assetName = getAssetName(title ?: fullName)
            CollectibleDetailPreview(
                isLoadingVisible = false,
                nftName = assetName,
                collectionNameOfNFT = collectionName,
                optedInAccountTypeDrawableResId = getAccountIconResourceByAccountType(accountDetail.accountType).iconResId,
                optedInAccountDisplayName = getAccountDisplayName(accountDetail),
                formattedNFTAmount = getFormattedCollectibleAmount(collectibleDetail, accountCollectibleDetail),
                mediaListOfNFT = getCollectibleMediaItems(collectibleDetail, isOwnedByTheUser, assetName),
                traitListOfNFT = collectibleInfo.traits?.map { collectibleTraitItemMapper(it) },
                nftDescription = collectibleDetail.collectibleInfo.collectibleDescription,
                creatorAccountAddressOfNFT = getAccountDisplayName(creatorAddress.orEmpty()),
                nftId = collectibleDetail.id,
                formattedTotalSupply = collectibleAmountFormatter(
                    collectibleDetail.assetInfo?.supply?.total,
                    getDecimalsOrZero()
                ),
                peraExplorerUrl = assetInfo?.explorerUrl.orEmpty(),
                isPureNFT = isPure(),
                primaryWarningResId = collectibleDetailWarningTextMapper.mapToWarningTextResId(prismUrl),
                secondaryWarningResId = collectibleDetailWarningTextMapper.mapToOptedInWarningTextResId(
                    isOwnedByTheUser = isOwnedByTheUser,
                    accountType = accountDetail.accountType
                ),
                isSendButtonVisible = isOwnedByTheUser && !isOwnedByWatchAccount,
                isOptOutButtonVisible = !isOwnedByTheUser && accountDetail.address != creatorAddress && !isOwnedByWatchAccount,
                globalErrorEvent = null,
                collectibleSendEvent = null
            )
        }
    }

    private fun getCollectibleMediaItems(
        collectibleDetail: CollectibleDetail,
        isOptedInByAccount: Boolean,
        assetName: AssetName
    ): List<BaseCollectibleMediaItem> {
        return collectibleDetail.collectibleMedias.map {
            collectibleMediaItemMapper(
                CollectibleMediaItemMapperPayload(
                    baseCollectibleMedia = it,
                    shouldDecreaseOpacity = !isOptedInByAccount,
                    baseCollectibleDetail = collectibleDetail,
                    showMediaButtons = true,
                    assetName = assetName
                )
            )
        }.orEmpty()
    }

    private fun getFormattedCollectibleAmount(
        collectibleDetail: CollectibleDetail,
        accountCollectibleDetail: BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData?
    ): String {
        return collectibleAmountFormatter(
            nftAmount = accountCollectibleDetail?.amount,
            fractionalDecimal = collectibleDetail.getDecimalsOrZero(),
            formattedAmount = accountCollectibleDetail?.formattedAmount,
            formattedCompactAmount = accountCollectibleDetail?.formattedCompactAmount
        )
    }
}

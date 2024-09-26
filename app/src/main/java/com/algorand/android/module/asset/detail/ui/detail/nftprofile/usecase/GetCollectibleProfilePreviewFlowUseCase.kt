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

package com.algorand.android.module.asset.detail.ui.detail.nftprofile.usecase

import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAssetName
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.assetdetail.component.collectible.domain.usecase.FetchCollectibleDetail
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.mapper.AsaStatusPreviewMapper
import com.algorand.android.module.asset.detail.ui.detail.asaprofile.model.AsaStatusPreview
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.mapper.CollectibleAmountFormatter
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.mapper.CollectibleDetailWarningTextMapper
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.mapper.CollectibleMediaItemMapper
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.mapper.CollectibleTraitItemMapper
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.BaseCollectibleMediaItem
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.CollectibleMediaItemMapperPayload
import com.algorand.android.module.asset.detail.ui.detail.nftprofile.model.CollectibleProfilePreview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetCollectibleProfilePreviewFlowUseCase @Inject constructor(
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val fetchCollectibleDetail: FetchCollectibleDetail,
    private val asaStatusPreviewMapper: AsaStatusPreviewMapper,
    private val getAssetName: GetAssetName,
    private val collectibleTraitItemMapper: CollectibleTraitItemMapper,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val collectibleAmountFormatter: CollectibleAmountFormatter,
    private val collectibleDetailWarningTextMapper: CollectibleDetailWarningTextMapper,
    private val collectibleMediaItemMapper: CollectibleMediaItemMapper
) : GetCollectibleProfilePreviewFlow {

    override suspend fun invoke(nftId: Long, accountAddress: String): Flow<CollectibleProfilePreview?> {
        return getAccountInformationFlow(accountAddress).map { accountInformation ->
            if (accountInformation == null) return@map null
            // TODO LOADING STATE
            fetchCollectibleDetail(nftId).use(
                onSuccess = {
                    createPreview(accountInformation, it)
                },
                onFailed = { _, _ -> null }
            )
        }
    }

    private suspend fun createPreview(
        accountInformation: AccountInformation,
        collectibleDetail: CollectibleDetail
    ): CollectibleProfilePreview {
        return with(collectibleDetail) {
            val assetName = getAssetName(title ?: fullName)
            CollectibleProfilePreview(
                isLoadingVisible = false,
                nftName = assetName,
                nftShortName = getAssetName(shortName),
                collectionNameOfNFT = collectionName,
                mediaListOfNFT = getCollectibleMediaItems(
                    collectibleDetail,
                    accountInformation.hasAsset(id),
                    assetName
                ),
                traitListOfNFT = collectibleDetail.collectibleInfo.traits?.map { collectibleTraitItemMapper(it) },
                nftDescription = collectibleDetail.collectibleInfo.collectibleDescription,
                creatorAccountAddressOfNFT = getAccountDisplayName(creatorAddress.orEmpty()),
                nftId = id,
                formattedTotalSupply = collectibleAmountFormatter(assetInfo?.supply?.total, getDecimalsOrZero()),
                peraExplorerUrl = assetInfo?.explorerUrl.orEmpty(),
                isPureNFT = isPure(),
                primaryWarningResId = collectibleDetailWarningTextMapper.mapToWarningTextResId(prismUrl),
                secondaryWarningResId = null,
                collectibleStatusPreview = createAsaStatusPreview(accountInformation, collectibleDetail),
                accountAddress = accountInformation.address
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

    private suspend fun createAsaStatusPreview(
        accountInformation: AccountInformation,
        collectibleDetail: CollectibleDetail
    ): AsaStatusPreview? {
        val isOptedInByAccount = accountInformation.hasAsset(assetId = collectibleDetail.id)
        val isOwnedByTheUser = accountInformation.hasAssetAmount(assetId = collectibleDetail.id)
        val accountAddress = accountInformation.address
        val isAssetCreatedByThisAccount = collectibleDetail.creatorAddress == accountAddress
        return with(asaStatusPreviewMapper) {
            when {
                !isOptedInByAccount -> mapToCollectibleAdditionStatus(accountAddress)
                !isOwnedByTheUser && isAssetCreatedByThisAccount -> mapToCollectibleRemovalStatus(accountAddress)
                else -> null
            }
        }
    }
}

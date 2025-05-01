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

package com.algorand.android.modules.collectibles.profile.ui.usecase

import com.algorand.android.R
import com.algorand.android.mapper.AssetActionMapper
import com.algorand.android.models.AssetAction
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.assets.core.ui.domain.usecase.GetAssetName
import com.algorand.android.modules.assets.profile.asaprofile.ui.mapper.AsaStatusPreviewMapper
import com.algorand.android.modules.assets.profile.asaprofile.ui.model.AsaStatusPreview
import com.algorand.android.modules.assets.profile.asaprofile.ui.model.PeraButtonState
import com.algorand.android.modules.collectibles.profile.ui.mapper.CollectibleProfilePreviewMapper
import com.algorand.android.modules.collectibles.profile.ui.model.CollectibleProfilePreview
import com.algorand.android.usecase.AccountAddressUseCase
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldingFlow
import com.algorand.wallet.asset.domain.usecase.FetchCollectibleDetail
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SuppressWarnings("LongParameterList")
class CollectibleProfilePreviewUseCase @Inject constructor(
    private val accountAddressUseCase: AccountAddressUseCase,
    private val asaStatusPreviewMapper: AsaStatusPreviewMapper,
    private val collectibleProfilePreviewMapper: CollectibleProfilePreviewMapper,
    private val assetActionMapper: AssetActionMapper,
    private val getAssetName: GetAssetName,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val fetchCollectibleDetail: FetchCollectibleDetail,
    private val getAccountAssetHoldingFlow: GetAccountAssetHoldingFlow
) {

    fun createAssetAction(assetId: Long, collectibleFullName: String?, accountAddress: String?): AssetAction {
        return assetActionMapper.mapTo(
            assetId = assetId,
            assetName = getAssetName(collectibleFullName),
            accountAddress = accountAddress
        )
    }

    fun getCollectibleProfilePreviewFlow(nftId: Long, accountAddress: String): Flow<CollectibleProfilePreview?> {
        return getAccountAssetHoldingFlow(accountAddress, nftId).map { assetHolding ->
            fetchCollectibleDetail(nftId).map { nftDetail ->
                val isOptedInByAccount = assetHolding != null
                val isUserHasCollectibleBalance = assetHolding != null && assetHolding.amount > BigInteger.ZERO
                val asaStatusPreview = createAsaStatusPreview(
                    isUserHasCollectibleBalance = isUserHasCollectibleBalance,
                    isCollectibleOptedInByAccount = isOptedInByAccount,
                    accountAddress = accountAddress,
                    creatorWalletAddress = nftDetail.creatorAddress
                )
                collectibleProfilePreviewMapper.mapToCollectibleProfilePreview(
                    collectibleDetail = nftDetail,
                    isOptedInByAccount = isOptedInByAccount,
                    asaStatusPreview = asaStatusPreview,
                    accountAddress = accountAddress,
                    nftName = getAssetName(nftDetail.title ?: nftDetail.fullName.orEmpty()),
                    creatorAccountAddressOfNFT = getAccountDisplayName(nftDetail.creatorAddress.orEmpty())
                )
            }.getDataOrNull()
        }
    }

    private suspend fun createAsaStatusPreview(
        isUserHasCollectibleBalance: Boolean,
        accountAddress: String,
        isCollectibleOptedInByAccount: Boolean,
        creatorWalletAddress: String?
    ): AsaStatusPreview? {
        return when {
            !isCollectibleOptedInByAccount -> {
                asaStatusPreviewMapper.mapToAsaAdditionStatusPreview(
                    accountAddress = accountAddressUseCase.getAccountAddress(accountAddress),
                    statusLabelTextResId = R.string.you_can_opt_in_to_this_nft,
                    peraButtonState = PeraButtonState.ADDITION,
                    actionButtonTextResId = R.string.opt_dash_in
                )
            }
            !isUserHasCollectibleBalance && creatorWalletAddress != accountAddress -> {
                asaStatusPreviewMapper.mapToCollectibleRemovalStatusPreview(
                    statusLabelTextResId = R.string.opted_in_to,
                    peraButtonState = PeraButtonState.REMOVAL,
                    actionButtonTextResId = R.string.remove,
                    accountAddress = accountAddressUseCase.getAccountAddress(accountAddress)
                )
            }
            else -> null
        }
    }
}

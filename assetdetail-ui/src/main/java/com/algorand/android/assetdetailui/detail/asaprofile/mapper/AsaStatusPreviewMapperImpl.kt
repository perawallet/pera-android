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

package com.algorand.android.assetdetailui.detail.asaprofile.mapper

import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.assetdetailui.detail.asaprofile.model.AsaStatusPreview
import com.algorand.android.assetdetailui.detail.asaprofile.model.AsaStatusPreview.RemovalStatus.CollectibleRemovalStatus
import com.algorand.android.assetdetailui.detail.asaprofile.model.PeraButtonState
import com.algorand.android.designsystem.R
import javax.inject.Inject

internal class AsaStatusPreviewMapperImpl @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : AsaStatusPreviewMapper {

    override suspend fun invoke(
        isAlgo: Boolean,
        isUserOptedInAsset: Boolean,
        accountAddress: String?,
        hasUserAmount: Boolean,
        formattedAccountBalance: String?,
        assetShortName: AssetName?
    ): AsaStatusPreview? {
        return when {
            isAlgo -> null
            accountAddress.isNullOrBlank() -> getAccountSelectionStatus()
            !isUserOptedInAsset -> getAdditionStatus(accountAddress)
            isUserOptedInAsset && hasUserAmount -> getTransferStatus(formattedAccountBalance, assetShortName)
            isUserOptedInAsset -> getAssetRemovalStatus(formattedAccountBalance, assetShortName)
            else -> null
        }
    }

    override suspend fun mapToCollectibleAdditionStatus(accountAddress: String): AsaStatusPreview.AdditionStatus {
        return AsaStatusPreview.AdditionStatus(
            statusLabelTextResId = R.string.you_can_opt_in_to_this_nft,
            peraButtonState = PeraButtonState.ADDITION,
            actionButtonTextResId = R.string.opt_dash_in,
            accountDisplayName = getAccountDisplayName(accountAddress),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress)
        )
    }

    override suspend fun mapToCollectibleRemovalStatus(accountAddress: String): CollectibleRemovalStatus {
        return CollectibleRemovalStatus(
            statusLabelTextResId = R.string.opted_in_to,
            peraButtonState = PeraButtonState.REMOVAL,
            actionButtonTextResId = R.string.remove,
            accountDisplayName = getAccountDisplayName(accountAddress),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress)
        )
    }

    private fun getAccountSelectionStatus(): AsaStatusPreview.AccountSelectionStatus {
        return AsaStatusPreview.AccountSelectionStatus(
            statusLabelTextResId = R.string.you_can_opt_in_to_this,
            peraButtonState = PeraButtonState.ADDITION,
            actionButtonTextResId = R.string.opt_dash_in
        )
    }

    private suspend fun getAdditionStatus(address: String): AsaStatusPreview.AdditionStatus {
        return AsaStatusPreview.AdditionStatus(
            statusLabelTextResId = R.string.you_can_add_this_asset,
            peraButtonState = PeraButtonState.ADDITION,
            actionButtonTextResId = R.string.opt_dash_in,
            accountDisplayName = getAccountDisplayName(address),
            accountIconDrawablePreview = getAccountIconDrawablePreview(address)
        )
    }

    private fun getTransferStatus(
        formattedAccountBalance: String?,
        assetShortName: AssetName?
    ): AsaStatusPreview.TransferStatus {
        return AsaStatusPreview.TransferStatus(
            statusLabelTextResId = R.string.balance,
            peraButtonState = PeraButtonState.REMOVAL,
            actionButtonTextResId = R.string.remove,
            formattedAccountBalance = formattedAccountBalance.orEmpty(),
            assetShortName = assetShortName
        )
    }

    private fun getAssetRemovalStatus(
        formattedAccountBalance: String?,
        assetShortName: AssetName?
    ): AsaStatusPreview.RemovalStatus.AssetRemovalStatus {
        return AsaStatusPreview.RemovalStatus.AssetRemovalStatus(
            statusLabelTextResId = R.string.balance,
            peraButtonState = PeraButtonState.REMOVAL,
            actionButtonTextResId = R.string.remove,
            formattedAccountBalance = formattedAccountBalance.orEmpty(),
            assetShortName = assetShortName
        )
    }
}

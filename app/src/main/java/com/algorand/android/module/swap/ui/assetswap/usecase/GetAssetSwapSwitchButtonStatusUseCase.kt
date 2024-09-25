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

package com.algorand.android.module.swap.ui.assetswap.usecase

import com.algorand.android.accountinfo.component.domain.usecase.IsAssetOwnedByAccount
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import javax.inject.Inject

internal class GetAssetSwapSwitchButtonStatusUseCase @Inject constructor(
    private val getFromSelectedAssetDetail: GetFromSelectedAssetDetail,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount
) : GetAssetSwapSwitchButtonStatus {

    override suspend fun invoke(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long,
        fromSelectedAssetDetail: AssetSwapPreview.SelectedAssetDetail
    ): Boolean {
        val fromAsset = getFromSelectedAssetDetail(fromAssetId, accountAddress, fromSelectedAssetDetail)
        val fromAssetVerificationTier = fromAsset.verificationTierConfiguration.toVerificationTier()
        val hasUserToAssetBalance = isAssetOwnedByAccount(accountAddress, toAssetId)
        return hasUserToAssetBalance && fromAssetVerificationTier.isValid()
    }

    private fun VerificationTier.isValid() = this == VerificationTier.VERIFIED || this == VerificationTier.TRUSTED
}

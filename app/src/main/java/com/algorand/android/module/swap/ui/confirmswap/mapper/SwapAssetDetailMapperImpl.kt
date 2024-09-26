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

package com.algorand.android.module.swap.ui.confirmswap.mapper

import com.algorand.android.module.account.core.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.module.account.core.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.module.swap.ui.confirmswap.model.ConfirmSwapPreview
import javax.inject.Inject

internal class SwapAssetDetailMapperImpl @Inject constructor(
    private val getAssetDrawableProvider: GetAssetDrawableProvider,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper
) : SwapAssetDetailMapper {

    override suspend operator fun invoke(
        assetId: Long,
        formattedAmount: String,
        formattedApproximateValue: String,
        shortName: AssetName,
        verificationTier: VerificationTier,
        amountTextColorResId: Int,
        approximateValueTextColorResId: Int
    ): ConfirmSwapPreview.SwapAssetDetail {
        return ConfirmSwapPreview.SwapAssetDetail(
            formattedAmount = formattedAmount,
            formattedApproximateValue = formattedApproximateValue,
            shortName = shortName,
            assetDrawableProvider = getAssetDrawableProvider(assetId),
            verificationTierConfig = verificationTierConfigurationMapper(verificationTier),
            amountTextColorResId = amountTextColorResId,
            approximateValueTextColorResId = approximateValueTextColorResId
        )
    }
}

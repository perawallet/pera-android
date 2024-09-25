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

import com.algorand.android.accountcore.ui.asset.assetdrawable.GetAssetDrawableProvider
import com.algorand.android.accountcore.ui.mapper.VerificationTierConfigurationMapper
import com.algorand.android.accountcore.ui.usecase.GetAssetName
import com.algorand.android.assetdetail.component.AssetConstants.DEFAULT_ASSET_DECIMAL
import com.algorand.android.assetdetail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.module.swap.component.domain.model.SwapQuoteAssetDetail
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview.SelectedAssetDetail
import com.algorand.android.utils.formatAsTwoDecimals
import java.math.BigDecimal
import javax.inject.Inject

internal class GetSelectedAssetDetailUseCase @Inject constructor(
    private val getAssetName: GetAssetName,
    private val getAssetDrawableProvider: GetAssetDrawableProvider,
    private val verificationTierConfigurationMapper: VerificationTierConfigurationMapper,
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData
) : GetSelectedAssetDetail {

    override suspend fun invoke(ownedAssetData: OwnedAssetData): SelectedAssetDetail {
        return SelectedAssetDetail(
            assetId = ownedAssetData.id,
            formattedBalance = ownedAssetData.formattedAmount,
            assetShortName = getAssetName(ownedAssetData.shortName),
            verificationTierConfiguration = verificationTierConfigurationMapper(ownedAssetData.verificationTier),
            assetDrawableProvider = getAssetDrawableProvider(ownedAssetData.id),
            assetDecimal = ownedAssetData.decimals
        )
    }

    override suspend fun invoke(assetDetail: AssetDetail): SelectedAssetDetail {
        return SelectedAssetDetail(
            assetId = assetDetail.id,
            formattedBalance = BigDecimal.ZERO.formatAsTwoDecimals(),
            assetShortName = getAssetName(assetDetail.assetInfo?.name?.shortName),
            verificationTierConfiguration = verificationTierConfigurationMapper(assetDetail.verificationTier),
            assetDrawableProvider = getAssetDrawableProvider(assetDetail.id),
            assetDecimal = assetDetail.getDecimalsOrZero()
        )
    }

    override suspend fun invoke(
        accountAddress: String,
        swapQuoteAssetDetail: SwapQuoteAssetDetail
    ): SelectedAssetDetail {
        val ownedAssetData = getAccountOwnedAssetData(accountAddress, swapQuoteAssetDetail.assetId, includeAlgo = true)
        return SelectedAssetDetail(
            assetId = swapQuoteAssetDetail.assetId,
            formattedBalance = ownedAssetData?.formattedAmount ?: BigDecimal.ZERO.formatAsTwoDecimals(),
            assetShortName = getAssetName(swapQuoteAssetDetail.shortName),
            verificationTierConfiguration = verificationTierConfigurationMapper(swapQuoteAssetDetail.verificationTier),
            assetDrawableProvider = getAssetDrawableProvider(swapQuoteAssetDetail.assetId),
            assetDecimal = ownedAssetData?.decimals ?: DEFAULT_ASSET_DECIMAL
        )
    }
}

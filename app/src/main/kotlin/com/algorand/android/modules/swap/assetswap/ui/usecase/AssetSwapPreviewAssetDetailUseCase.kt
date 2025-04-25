/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.swap.assetswap.ui.usecase

import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountOwnedAssetsData
import com.algorand.android.modules.swap.assetswap.domain.model.SwapQuoteAssetDetail
import com.algorand.android.modules.swap.assetswap.ui.mapper.SelectedAssetDetailMapper
import com.algorand.android.modules.swap.assetswap.ui.model.AssetSwapPreview
import com.algorand.android.utils.DEFAULT_ASSET_DECIMAL
import com.algorand.android.utils.formatAsTwoDecimals
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import java.math.BigDecimal
import javax.inject.Inject

class AssetSwapPreviewAssetDetailUseCase @Inject constructor(
    private val selectedAssetDetailMapper: SelectedAssetDetailMapper,
    private val getAccountOwnedAssetsData: GetAccountOwnedAssetsData,
    private val fetchAsset: FetchAsset
) {

    suspend fun createFromSelectedAssetDetail(
        fromAssetId: Long,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): AssetSwapPreview.SelectedAssetDetail {
        val isFromAssetHasChanged = previousState.fromSelectedAssetDetail.assetId != fromAssetId
        return if (isFromAssetHasChanged) {
            val ownedAssetData = getAccountOwnedAssetsData(accountAddress, true).run {
                firstOrNull { fromAssetId == it.id } ?: first { it.isAlgo }
            }
            createSelectedAssetDetail(ownedAssetData)
        } else {
            previousState.fromSelectedAssetDetail
        }
    }

    suspend fun createToSelectedAssetDetail(
        toAssetId: Long?,
        accountAddress: String,
        previousState: AssetSwapPreview
    ): AssetSwapPreview.SelectedAssetDetail? {
        val isToAssetHasChanged = previousState.toSelectedAssetDetail?.assetId != toAssetId
        if (toAssetId == null) return null
        return if (isToAssetHasChanged) {
            val ownedAssetData = getAccountOwnedAssetsData(accountAddress, true).run {
                firstOrNull { toAssetId == it.id }
            }
            if (ownedAssetData == null) {
                val assetDetail = fetchAsset(toAssetId).getDataOrNull()
                createSelectedAssetDetail(assetDetail ?: return null)
            } else {
                createSelectedAssetDetail(ownedAssetData)
            }
        } else {
            previousState.toSelectedAssetDetail
        }
    }

    suspend fun createSelectedAssetDetailFromSwapQuoteAssetDetail(
        accountAddress: String,
        swapQuoteAssetDetail: SwapQuoteAssetDetail
    ): AssetSwapPreview.SelectedAssetDetail {
        val ownedAssetData = getAccountOwnedAssetsData(accountAddress, true).run {
            firstOrNull { swapQuoteAssetDetail.assetId == it.id }
        }
        return selectedAssetDetailMapper.mapToSelectedAssetDetail(
            assetId = swapQuoteAssetDetail.assetId,
            formattedBalance = ownedAssetData?.formattedAmount ?: BigDecimal.ZERO.formatAsTwoDecimals(),
            assetShortName = swapQuoteAssetDetail.shortName,
            verificationTier = swapQuoteAssetDetail.verificationTier,
            assetDecimal = ownedAssetData?.decimals ?: DEFAULT_ASSET_DECIMAL
        )
    }

    private suspend fun createSelectedAssetDetail(
        ownedAssetData: OwnedAssetData
    ): AssetSwapPreview.SelectedAssetDetail {
        return selectedAssetDetailMapper.mapToSelectedAssetDetail(
            assetId = ownedAssetData.id,
            formattedBalance = ownedAssetData.formattedAmount,
            assetShortName = ownedAssetData.shortName,
            verificationTier = ownedAssetData.verificationTier,
            assetDecimal = ownedAssetData.decimals
        )
    }

    private suspend fun createSelectedAssetDetail(asset: Asset): AssetSwapPreview.SelectedAssetDetail {
        return with(asset) {
            selectedAssetDetailMapper.mapToSelectedAssetDetail(
                assetId = id,
                formattedBalance = BigDecimal.ZERO.formatAsTwoDecimals(),
                assetShortName = shortName,
                verificationTier = verificationTier,
                assetDecimal = getDecimalsOrZero()
            )
        }
    }
}

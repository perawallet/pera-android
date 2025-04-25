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
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountOwnedAssetData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.currency.domain.usecase.DisplayedCurrencyUseCase
import com.algorand.android.modules.swap.assetswap.ui.mapper.AssetSwapPreviewMapper
import com.algorand.android.modules.swap.assetswap.ui.mapper.SelectedAssetAmountDetailMapper
import com.algorand.android.modules.swap.assetswap.ui.mapper.SelectedAssetDetailMapper
import com.algorand.android.modules.swap.assetswap.ui.model.AssetSwapPreview
import com.algorand.android.modules.swap.common.SwapAppxValueParityHelper
import com.algorand.android.utils.emptyString
import com.algorand.wallet.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject

class AssetSwapInitialPreviewUseCase @Inject constructor(
    private val selectedAssetDetailMapper: SelectedAssetDetailMapper,
    private val selectedAssetAmountDetailMapper: SelectedAssetAmountDetailMapper,
    private val assetSwapPreviewMapper: AssetSwapPreviewMapper,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount,
    private val swapAppxValueParityHelper: SwapAppxValueParityHelper,
    private val displayedCurrencyUseCase: DisplayedCurrencyUseCase,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData,
    private val getAccountDisplayName: GetAccountDisplayName
) {

    suspend fun getAssetSwapPreviewInitializationState(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long?
    ): AssetSwapPreview? {
        val fromAssetDetail = getFromAssetDetail(accountAddress, fromAssetId) ?: return null
        val toAssetDetail = getToAssetDetail(accountAddress, toAssetId)

        val fromSelectedAssetAmountDetail = selectedAssetAmountDetailMapper.mapToDefaultSelectedAssetAmountDetail(
            primaryCurrencySymbol = swapAppxValueParityHelper.getDisplayedCurrencySymbol()
        )
        val toSelectedAssetAmountDetail = selectedAssetAmountDetailMapper.mapToDefaultSelectedAssetAmountDetail(
            primaryCurrencySymbol = displayedCurrencyUseCase.getDisplayedCurrencySymbol()
        )

        // TODO update isSwitchAssetsButtonEnabled when we merge tinyman-swap-2
        return assetSwapPreviewMapper.mapToAssetSwapPreview(
            accountDisplayName = getAccountDisplayName(accountAddress),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress),
            fromSelectedAssetDetail = fromAssetDetail,
            toSelectedAssetDetail = toAssetDetail,
            isSwapButtonEnabled = false,
            isLoadingVisible = false,
            fromSelectedAssetAmountDetail = fromSelectedAssetAmountDetail,
            toSelectedAssetAmountDetail = toSelectedAssetAmountDetail,
            isSwitchAssetsButtonEnabled = if (toAssetId == null) {
                false
            } else {
                isAssetOwnedByAccount(accountAddress, toAssetId)
            },
            isMaxAndPercentageButtonEnabled = toAssetDetail != null,
            errorEvent = null,
            swapQuote = null,
            clearToSelectedAssetDetailEvent = null,
            navigateToConfirmSwapFragmentEvent = null,
            formattedPercentageText = emptyString()
        )
    }

    private suspend fun getFromAssetDetail(
        accountAddress: String,
        assetId: Long
    ): AssetSwapPreview.SelectedAssetDetail? {
        val ownedFromAssetDetail = getAccountOwnedAssetData(accountAddress, assetId)
            ?: getAccountOwnedAssetData(accountAddress, ALGO_ID)
            ?: return null
        return getAssetDetail(ownedFromAssetDetail)
    }

    private suspend fun getToAssetDetail(
        accountAddress: String,
        assetId: Long?
    ): AssetSwapPreview.SelectedAssetDetail? {
        if (assetId == null) return null
        val ownedToAssetDetail = getAccountOwnedAssetData(accountAddress, assetId) ?: return null
        return getAssetDetail(ownedToAssetDetail)
    }

    private suspend fun getAssetDetail(ownedAssetData: OwnedAssetData): AssetSwapPreview.SelectedAssetDetail {
        return selectedAssetDetailMapper.mapToSelectedAssetDetail(
            assetId = ownedAssetData.id,
            formattedBalance = ownedAssetData.formattedAmount,
            assetShortName = ownedAssetData.shortName,
            verificationTier = ownedAssetData.verificationTier,
            assetDecimal = ownedAssetData.decimals
        )
    }
}

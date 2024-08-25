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

package com.algorand.android.swapui.assetswap.usecase

import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.accountinfo.component.domain.usecase.IsAssetOwnedByAccount
import com.algorand.android.parity.domain.usecase.GetDisplayedCurrencySymbol
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencySymbolOrName
import com.algorand.android.swapui.assetswap.model.AssetSwapPreview
import com.algorand.android.swapui.assetswap.usecase.main.GetSwapInitialPreview
import javax.inject.Inject

internal class GetSwapInitialPreviewUseCase @Inject constructor(
    private val getFromSelectedAssetDetail: GetFromSelectedAssetDetail,
    private val getToSelectedAssetDetail: GetToSelectedAssetDetail,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getSelectedAssetAmountDetail: GetSelectedAssetAmountDetail,
    private val getPrimaryCurrencySymbolOrName: GetPrimaryCurrencySymbolOrName,
    private val getDisplayedCurrencySymbol: GetDisplayedCurrencySymbol,
    private val isAssetOwnedByAccount: IsAssetOwnedByAccount
) : GetSwapInitialPreview {

    override suspend fun invoke(accountAddress: String, fromAssetId: Long, toAssetId: Long?): AssetSwapPreview {
        val fromAssetDetail = getFromSelectedAssetDetail(fromAssetId, accountAddress)
        val toAssetDetail = getToSelectedAssetDetail(toAssetId, accountAddress)

        // TODO update isSwitchAssetsButtonEnabled when we merge tinyman-swap-2
        return AssetSwapPreview(
            accountDisplayName = getAccountDisplayName(accountAddress),
            accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress),
            fromSelectedAssetDetail = fromAssetDetail,
            toSelectedAssetDetail = toAssetDetail,
            isSwapButtonEnabled = false,
            isLoadingVisible = false,
            fromSelectedAssetAmountDetail = getDefaultFromSelectedAssetAmountDetail(),
            toSelectedAssetAmountDetail = getDefaultToSelectedAssetAmountDetail(),
            isSwitchAssetsButtonEnabled = isSwitchAssetsButtonEnabled(accountAddress, toAssetId),
            isMaxAndPercentageButtonEnabled = toAssetDetail != null,
            errorEvent = null,
            swapQuote = null,
            clearToSelectedAssetDetailEvent = null,
            navigateToConfirmSwapFragmentEvent = null,
            formattedPercentageText = ""
        )
    }

    private suspend fun isSwitchAssetsButtonEnabled(accountAddress: String, toAssetId: Long?): Boolean {
        return if (toAssetId == null) {
            false
        } else {
            isAssetOwnedByAccount(accountAddress, toAssetId)
        }
    }

    private suspend fun getDefaultFromSelectedAssetAmountDetail(): AssetSwapPreview.SelectedAssetAmountDetail {
        return getSelectedAssetAmountDetail(primaryCurrencySymbol = getPrimaryCurrencySymbolOrName())
    }

    private suspend fun getDefaultToSelectedAssetAmountDetail(): AssetSwapPreview.SelectedAssetAmountDetail {
        return getSelectedAssetAmountDetail(primaryCurrencySymbol = getDisplayedCurrencySymbol())
    }
}

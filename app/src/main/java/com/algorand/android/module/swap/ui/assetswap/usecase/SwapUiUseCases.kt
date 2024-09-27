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

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.component.domain.model.SwapQuoteAssetDetail
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview.SelectedAssetAmountDetail
import com.algorand.android.module.swap.ui.assetswap.model.AssetSwapPreview.SelectedAssetDetail
import com.algorand.android.module.swap.ui.assetswap.model.GetSwapQuoteUpdatedPreviewPayload
import com.algorand.android.module.swap.ui.assetswap.model.SwapError

internal interface GetSwapQuoteUpdatedPreview {
    suspend operator fun invoke(payload: GetSwapQuoteUpdatedPreviewPayload): AssetSwapPreview?
}

internal interface GetSwapBalanceError {
    suspend operator fun invoke(swapQuote: SwapQuote, accountAddress: String): Event<SwapError>?
}

internal interface GetAssetSwapSwitchButtonStatus {
    suspend operator fun invoke(
        accountAddress: String,
        fromAssetId: Long,
        toAssetId: Long,
        fromSelectedAssetDetail: SelectedAssetDetail
    ): Boolean
}

internal interface GetFromSelectedAssetDetail {
    suspend operator fun invoke(
        fromAssetId: Long,
        accountAddress: String,
        selectedAssetDetail: SelectedAssetDetail
    ): SelectedAssetDetail

    suspend operator fun invoke(
        fromAssetId: Long,
        accountAddress: String,
    ): SelectedAssetDetail
}

internal interface GetToSelectedAssetDetail {
    suspend operator fun invoke(
        toAssetId: Long?,
        accountAddress: String,
        selectedAssetDetail: SelectedAssetDetail?
    ): SelectedAssetDetail?

    suspend operator fun invoke(
        toAssetId: Long?,
        accountAddress: String
    ): SelectedAssetDetail?
}

internal interface GetSelectedAssetDetail {
    suspend operator fun invoke(ownedAssetData: OwnedAssetData): SelectedAssetDetail
    suspend operator fun invoke(assetDetail: AssetDetail): SelectedAssetDetail
    suspend operator fun invoke(accountAddress: String, swapQuoteAssetDetail: SwapQuoteAssetDetail): SelectedAssetDetail
}

internal interface GetSelectedAssetAmountDetail {
    suspend operator fun invoke(
        amount: String?,
        formattedApproximateValue: String,
        assetDecimal: Int
    ): SelectedAssetAmountDetail

    suspend operator fun invoke(
        amount: String? = null,
        formattedApproximateValue: String? = null,
        assetDecimal: Int? = null,
        primaryCurrencySymbol: String
    ): SelectedAssetAmountDetail
}

internal interface GetFromSelectedAssetAmountDetail {
    suspend operator fun invoke(
        swapQuote: SwapQuote,
        previousAmount: String?
    ): SelectedAssetAmountDetail
}

internal interface GetToSelectedAssetAmountDetail {
    suspend operator fun invoke(swapQuote: SwapQuote): SelectedAssetAmountDetail
}

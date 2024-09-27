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

package com.algorand.android.module.swap.ui.assetswap.model

import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.module.account.core.ui.model.AssetName
import com.algorand.android.module.account.core.ui.model.VerificationTierConfiguration
import com.algorand.android.module.drawable.asset.BaseAssetDrawableProvider
import com.algorand.android.foundation.Event
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.utils.formatAmount

data class AssetSwapPreview(
    val accountDisplayName: AccountDisplayName,
    val accountIconDrawablePreview: AccountIconDrawablePreview,
    val fromSelectedAssetDetail: SelectedAssetDetail,
    val toSelectedAssetDetail: SelectedAssetDetail?,
    val isSwapButtonEnabled: Boolean,
    val isLoadingVisible: Boolean,
    val fromSelectedAssetAmountDetail: SelectedAssetAmountDetail?,
    val toSelectedAssetAmountDetail: SelectedAssetAmountDetail?,
    val isSwitchAssetsButtonEnabled: Boolean,
    val isMaxAndPercentageButtonEnabled: Boolean,
    val formattedPercentageText: String,
    val errorEvent: Event<SwapError>?,
    val swapQuote: SwapQuote?,
    val clearToSelectedAssetDetailEvent: Event<Unit>?,
    val navigateToConfirmSwapFragmentEvent: Event<SwapQuote>?
) {

    data class SelectedAssetDetail(
        val assetId: Long,
        val formattedBalance: String,
        val assetShortName: AssetName,
        val verificationTierConfiguration: VerificationTierConfiguration,
        val assetDrawableProvider: BaseAssetDrawableProvider,
        val assetDecimal: Int
    )

    data class SelectedAssetAmountDetail(
        val amount: String?,
        val formattedApproximateValue: String,
        val assetDecimal: Int
    ) {

        val formattedAmount: String?
            get() = amount?.toBigDecimalOrNull()?.formatAmount(assetDecimal, false)
    }
}

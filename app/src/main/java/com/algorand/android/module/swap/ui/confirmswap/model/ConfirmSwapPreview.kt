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

package com.algorand.android.module.swap.ui.confirmswap.model

import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AccountIconDrawablePreview
import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider
import com.algorand.android.foundation.Event
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.ui.assetswap.model.SwapError

data class ConfirmSwapPreview(
    val fromAssetDetail: SwapAssetDetail,
    val toAssetDetail: SwapAssetDetail,
    val slippageTolerance: String,
    val formattedPriceImpact: String,
    val minimumReceived: AnnotatedString,
    val formattedExchangeFee: String,
    val formattedPeraFee: String,
    val swapQuote: SwapQuote,
    val isLoading: Boolean,
    val priceImpactWarningStatus: ConfirmSwapPriceImpactWarningStatus,
    val accountIconDrawablePreview: AccountIconDrawablePreview,
    val accountDisplayName: AccountDisplayName,
    val errorEvent: Event<SwapError>? = null,
    val slippageToleranceUpdateSuccessEvent: Event<Unit>? = null,
    val navToSwapConfirmationBottomSheetEvent: Event<Long>? = null,
    private val priceRatioProvider: SwapPriceRatioProvider
) {

    fun getPriceRatio(): AnnotatedString {
        return priceRatioProvider.getRatioState()
    }

    fun getSwitchedPriceRatio(): AnnotatedString {
        return priceRatioProvider.getSwitchedRatioState()
    }

    data class SwapAssetDetail(
        val formattedAmount: String,
        val formattedApproximateValue: String,
        val shortName: AssetName,
        val assetDrawableProvider: BaseAssetDrawableProvider,
        val verificationTierConfig: VerificationTierConfiguration,
        val amountTextColorResId: Int,
        val approximateValueTextColorResId: Int
    )
}

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

package com.algorand.android.swapui.confirmswap.mapper

import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.swap.domain.model.SwapQuote
import com.algorand.android.swapui.confirmswap.model.ConfirmSwapPreview
import com.algorand.android.swapui.confirmswap.model.ConfirmSwapPriceImpactWarningStatus
import com.algorand.android.swapui.confirmswap.model.SignSwapTransactionResult
import com.algorand.android.swapui.confirmswap.model.SwapPriceRatioProvider
import com.algorand.android.transaction.domain.sign.model.SignTransactionResult

internal interface ConfirmSwapPriceImpactWarningStatusMapper {
    operator fun invoke(priceImpact: Float): ConfirmSwapPriceImpactWarningStatus
}

internal interface SwapAssetDetailMapper {
    suspend operator fun invoke(
        assetId: Long,
        formattedAmount: String,
        formattedApproximateValue: String,
        shortName: AssetName,
        verificationTier: VerificationTier,
        amountTextColorResId: Int,
        approximateValueTextColorResId: Int
    ): ConfirmSwapPreview.SwapAssetDetail
}

internal interface SwapPriceRatioProviderMapper {
    operator fun invoke(swapQuote: SwapQuote): SwapPriceRatioProvider
}

internal interface SignSwapTransactionResultMapper {
    operator fun invoke(result: SignTransactionResult): SignSwapTransactionResult?
}

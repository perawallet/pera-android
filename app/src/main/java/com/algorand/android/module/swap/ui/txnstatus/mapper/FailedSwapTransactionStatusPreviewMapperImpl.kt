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

package com.algorand.android.module.swap.ui.txnstatus.mapper

import com.algorand.android.R
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusPreview
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusType
import javax.inject.Inject

internal class FailedSwapTransactionStatusPreviewMapperImpl @Inject constructor() :
    FailedSwapTransactionStatusPreviewMapper {

    override fun invoke(swapQuote: SwapQuote): SwapTransactionStatusPreview {
        return SwapTransactionStatusPreview(
            swapTransactionStatusType = SwapTransactionStatusType.FAILED,
            transactionStatusAnimationDrawableResId = R.drawable.ic_close,
            transactionStatusAnimationDrawableTintResId = R.color.background,
            transactionStatusAnimationBackgroundResId = R.drawable.bg_layer_gray_lighter_oval,
            transactionStatusAnimationBackgroundTintResId = R.color.negative,
            transactionStatusTitleAnnotatedString = getTitleAnnotatedString(swapQuote),
            transactionStatusDescriptionAnnotatedString = AnnotatedString(R.string.we_encountered_an_unexpected),
            isTransactionDetailGroupVisible = false,
            isPrimaryActionButtonVisible = true,
            isGoToHomepageButtonVisible = true,
            primaryActionButtonTextResId = R.string.try_again,
            secondaryActionButtonTextResId = R.string.go_to_homepage,
            transactionStatusAnimationResId = null,
            urlEncodedTransactionGroupId = null
        )
    }

    private fun getTitleAnnotatedString(swapQuote: SwapQuote): AnnotatedString {
        return AnnotatedString(
            stringResId = R.string.asset_pair_swap_has_failed,
            replacementList = listOf(
                "to_asset_name" to swapQuote.toAssetDetail.shortName,
                "from_asset_name" to swapQuote.fromAssetDetail.shortName
            )
        )
    }
}

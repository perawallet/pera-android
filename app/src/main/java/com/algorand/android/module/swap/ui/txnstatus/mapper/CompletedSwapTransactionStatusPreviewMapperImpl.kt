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

import android.text.style.ForegroundColorSpan
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.R
import com.algorand.android.module.drawable.core.ColorResourceProvider
import com.algorand.android.utils.formatAmount
import com.algorand.android.foundation.common.encodeToURL
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusNavArgs
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusPreview
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusType
import javax.inject.Inject

internal class CompletedSwapTransactionStatusPreviewMapperImpl @Inject constructor(
    private val colorResourceProvider: ColorResourceProvider
) : CompletedSwapTransactionStatusPreviewMapper {

    override fun invoke(args: SwapTransactionStatusNavArgs): SwapTransactionStatusPreview {
        with(args.swapQuote) {
            return SwapTransactionStatusPreview(
                swapTransactionStatusType = SwapTransactionStatusType.COMPLETED,
                transactionStatusAnimationDrawableResId = R.drawable.ic_check,
                transactionStatusAnimationDrawableTintResId = R.color.background,
                transactionStatusAnimationBackgroundResId = R.drawable.bg_layer_gray_lighter_oval,
                transactionStatusAnimationBackgroundTintResId = R.color.success,
                transactionStatusTitleAnnotatedString = getTitleAnnotatedString(getToAssetName(), getFromAssetName()),
                transactionStatusDescriptionAnnotatedString = getDescriptionAnnotatedString(args),
                isTransactionDetailGroupVisible = true,
                urlEncodedTransactionGroupId = args.transactionGroupId?.encodeToURL(),
                isPrimaryActionButtonVisible = true,
                isGoToHomepageButtonVisible = false,
                primaryActionButtonTextResId = R.string.done,
                transactionStatusAnimationResId = null,
                secondaryActionButtonTextResId = null
            )
        }
    }

    private fun SwapQuote.getToAssetName(): String {
        return toAssetDetail.shortName
    }

    private fun SwapQuote.getFromAssetName(): String {
        return fromAssetDetail.shortName
    }

    private fun getTitleAnnotatedString(toAssetName: String, fromAssetName: String): AnnotatedString {
        return AnnotatedString(
            stringResId = R.string.asset_pair_swap_completed,
            replacementList = listOf(
                "to_asset_name" to toAssetName,
                "from_asset_name" to fromAssetName
            )
        )
    }

    private fun getDescriptionAnnotatedString(args: SwapTransactionStatusNavArgs): AnnotatedString {
        return with(args.swapQuote) {
            val annotatedDescriptionTextColor = colorResourceProvider(R.color.text_main)
            val formattedFromAmount = fromAssetAmount.movePointLeft(fromAssetDetail.fractionDecimals)
                .formatAmount(fromAssetDetail.fractionDecimals, isDecimalFixed = false)
            AnnotatedString(
                stringResId = R.string.you_received_amount_asset_name_in,
                replacementList = listOf(
                    "to_amount_and_asset_name" to "${args.formattedReceivedAssetAmount} ${getToAssetName()}",
                    "from_amount_and_asset_name" to "$formattedFromAmount ${getFromAssetName()}"
                ),
                customAnnotationList = listOf(
                    "received_asset_color" to ForegroundColorSpan(annotatedDescriptionTextColor),
                    "paid_asset_color" to ForegroundColorSpan(annotatedDescriptionTextColor)
                )
            )
        }
    }
}

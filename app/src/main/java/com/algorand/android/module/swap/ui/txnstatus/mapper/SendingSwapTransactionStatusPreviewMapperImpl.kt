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

import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.R
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusPreview
import com.algorand.android.module.swap.ui.txnstatus.model.SwapTransactionStatusType
import javax.inject.Inject

internal class SendingSwapTransactionStatusPreviewMapperImpl @Inject constructor() :
    SendingSwapTransactionStatusPreviewMapper {

    override fun invoke(swapQuote: SwapQuote): SwapTransactionStatusPreview {
        val formattedAmount = swapQuote.getFormattedMinimumReceivedAmount()
        val formattedAmountAndAsset = "$formattedAmount ${swapQuote.toAssetDetail.shortName}"
        return SwapTransactionStatusPreview(
            swapTransactionStatusType = SwapTransactionStatusType.SENDING,
            transactionStatusAnimationResId = R.raw.transaction_sending_animation,
            transactionStatusAnimationBackgroundResId = R.drawable.bg_layer_gray_lighter_oval,
            transactionStatusAnimationBackgroundTintResId = R.color.black,
            transactionStatusTitleAnnotatedString = AnnotatedString(
                stringResId = R.string.sending_the_transaction
            ),
            transactionStatusDescriptionAnnotatedString = AnnotatedString(
                stringResId = R.string.you_will_receive_at_least,
                replacementList = listOf("amount_and_asset_name" to formattedAmountAndAsset)
            ),
            isTransactionDetailGroupVisible = false,
            isPrimaryActionButtonVisible = false,
            isGoToHomepageButtonVisible = false,
            transactionStatusAnimationDrawableResId = null,
            transactionStatusAnimationDrawableTintResId = null,
            urlEncodedTransactionGroupId = null,
            primaryActionButtonTextResId = null,
            secondaryActionButtonTextResId = null
        )
    }
}

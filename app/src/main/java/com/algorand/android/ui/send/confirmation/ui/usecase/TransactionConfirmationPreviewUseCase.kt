/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.send.confirmation.ui.usecase

import com.algorand.android.R
import com.algorand.android.ui.send.confirmation.ui.mapper.TransactionStatusPreviewMapper
import com.algorand.android.ui.send.confirmation.ui.model.TransactionStatusPreview
import javax.inject.Inject

class TransactionConfirmationPreviewUseCase @Inject constructor(
    private val transactionStatusPreviewMapper: TransactionStatusPreviewMapper
) {

    fun getTransactionLoadingPreview(): TransactionStatusPreview {
        return transactionStatusPreviewMapper.mapToTransactionStatusPreview(
            transactionStatusAnimationResId = R.raw.pera_transaction_loading_animation,
            transactionStatusAnimationBackgroundResId = R.drawable.bg_layer_oval,
            transactionStatusAnimationBackgroundTintResId = R.color.button_helper_bg,
            transactionStatusAnimationDrawableResId = null,
            transactionStatusAnimationDrawableTintResId = null,
            transactionStatusTitleResId = R.string.sending_the_transaction,
            transactionStatusDescriptionResId = R.string.your_transaction_is_being_processed,
            onExitSendAlgoNavigationEvent = null,
            isExplorerButtonVisible = false,
            isDoneButtonVisible = false
        )
    }

    fun getTransactionReceivedPreview(transactionId: String?): TransactionStatusPreview {
        return transactionStatusPreviewMapper.mapToTransactionStatusPreview(
            transactionStatusAnimationResId = null,
            transactionStatusAnimationBackgroundResId = R.drawable.bg_layer_oval,
            transactionStatusAnimationBackgroundTintResId = R.color.positive,
            transactionStatusAnimationDrawableResId = R.drawable.ic_check,
            transactionStatusAnimationDrawableTintResId = R.color.background,
            transactionStatusTitleResId = R.string.operation_completed,
            transactionStatusDescriptionResId = R.string.your_transaction_was,
            onExitSendAlgoNavigationEvent = null,
            isExplorerButtonVisible = transactionId.isNullOrEmpty().not(),
            isDoneButtonVisible = true
        )
    }
}

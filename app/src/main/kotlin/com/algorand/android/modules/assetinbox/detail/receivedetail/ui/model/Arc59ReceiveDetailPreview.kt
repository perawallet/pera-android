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

package com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model

import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.model.BaseArc59ClaimRejectTransaction
import com.algorand.android.modules.assetinbox.detail.transactiondetail.model.Arc59TransactionDetailArgs
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.android.utils.assetdrawable.BaseAssetDrawableProvider

data class Arc59ReceiveDetailPreview(
    val receiverAccountDetailPreview: ReceiverAccountDetailPreview,
    val assetPreviewDetail: AssetPreviewDetail,
    val arc59TransactionDetailArgs: Arc59TransactionDetailArgs,
    val claimTransaction: Event<List<BaseArc59ClaimRejectTransaction.Arc59ClaimTransaction>>?,
    val rejectTransaction: Event<List<BaseArc59ClaimRejectTransaction.Arc59RejectTransaction>>?,
    val showError: Event<ErrorResource>?,
    val isLoading: Boolean,
    val onTransactionSendSuccessfully: Event<String>?
) {

    data class AssetPreviewDetail(
        val name: AssetName,
        val shortName: String,
        val iconUrl: String?,
        val verificationTier: VerificationTierConfiguration,
        val drawableProvider: BaseAssetDrawableProvider,
        val firstSender: String?,
        val otherSendersCount: Int
    )
}

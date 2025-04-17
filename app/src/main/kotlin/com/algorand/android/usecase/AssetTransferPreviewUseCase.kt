/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.usecase

import com.algorand.android.mapper.AssetTransferPreviewMapper
import com.algorand.android.models.AssetTransferPreview
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.parity.domain.usecase.ParityUseCase
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.MIN_FEE
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import com.algorand.wallet.asset.domain.usecase.GetAsset
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class AssetTransferPreviewUseCase @Inject constructor(
    private val assetTransferPreviewMapper: AssetTransferPreviewMapper,
    private val parityUseCase: ParityUseCase,
    private val sendSignedTransactionUseCase: SendSignedTransactionUseCase,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAsset: GetAsset,
    private val fetchAsset: FetchAsset,
    private val getAccountDetail: GetAccountDetail
) {

    suspend fun getAssetTransferPreview(
        transactionDataList: List<TransactionSignData>,
        receiverMinBalanceFee: Long? = null
    ): AssetTransferPreview {
        val fee = transactionDataList.sumOf {
            it.calculatedFee ?: (it as? TransactionSignData.Send)?.projectedFee
            ?: MIN_FEE
        } + (receiverMinBalanceFee ?: 0)
        val sendTransactionData = transactionDataList.find {
            it is TransactionSignData.Send
        } as TransactionSignData.Send
        val exchangePrice = parityUseCase.getAlgoToPrimaryCurrencyConversionRate()
        val asset = getAsset(sendTransactionData.assetId) ?: fetchAsset(sendTransactionData.assetId).getDataOrNull()
        return assetTransferPreviewMapper.mapToAssetTransferPreview(
            transactionData = sendTransactionData,
            exchangePrice = exchangePrice,
            currencySymbol = parityUseCase.getPrimaryCurrencySymbolOrName(),
            note = sendTransactionData.xnote ?: sendTransactionData.note,
            isNoteEditable = sendTransactionData.xnote == null,
            accountIconDrawablePreview = getAccountIconDrawablePreview(sendTransactionData.senderAccountAddress),
            assetId = sendTransactionData.assetId,
            assetShortName = asset?.shortName ?: sendTransactionData.assetId.toString(),
            assetDecimals = asset?.assetInfo?.decimals ?: 0,
            fee = fee,
            targetAccountDetail = getAccountDetail(sendTransactionData.targetUser.publicKey)
        )
    }

    suspend fun sendSignedTransaction(
        signedTransactionDetail: SignedTransactionDetail
    ): Flow<DataResource<String>> {
        return sendSignedTransactionUseCase.sendSignedTransaction(signedTransactionDetail)
    }
}

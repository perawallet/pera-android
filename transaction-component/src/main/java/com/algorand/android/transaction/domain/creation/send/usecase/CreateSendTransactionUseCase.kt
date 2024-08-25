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

package com.algorand.android.transaction.domain.creation.send.usecase

import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.transaction.domain.creation.CreatePaymentTransaction
import com.algorand.android.transaction.domain.creation.CreateSendAssetTransaction
import com.algorand.android.transaction.domain.creation.CreateSendTransaction
import com.algorand.android.transaction.domain.creation.model.CreateTransactionResult
import com.algorand.android.transaction.domain.creation.model.CreateTransactionResult.NetworkError
import com.algorand.android.transaction.domain.creation.send.asset.mapper.CreateSendAssetTransactionPayloadMapper
import com.algorand.android.transaction.domain.creation.send.model.CreateSendTransactionPayload
import com.algorand.android.transaction.domain.creation.send.payment.mapper.CreatePaymentTransactionPayloadMapper
import com.algorand.android.transaction.domain.usecase.GetTransactionParams
import javax.inject.Inject

internal class CreateSendTransactionUseCase @Inject constructor(
    private val getTransactionParams: GetTransactionParams,
    private val createPaymentTransaction: CreatePaymentTransaction,
    private val createSendAssetTransaction: CreateSendAssetTransaction,
    private val paymentPayloadMapper: CreatePaymentTransactionPayloadMapper,
    private val assetPayloadMapper: CreateSendAssetTransactionPayloadMapper
) : CreateSendTransaction {

    override suspend fun invoke(payload: CreateSendTransactionPayload): CreateTransactionResult {
        val txnParamsResult = getTransactionParams()
        val txnParams = txnParamsResult.getDataOrNull()
            ?: return NetworkError(txnParamsResult.getExceptionOrNull()?.message)

        return if (payload.assetId == ALGO_ASSET_ID) {
            val paymentTxnPayload = paymentPayloadMapper(payload, txnParams)
            return createPaymentTransaction(paymentTxnPayload)
        } else {
            val assetTxnPayload = assetPayloadMapper(payload, txnParams)
            createSendAssetTransaction(assetTxnPayload)
        }
    }
}

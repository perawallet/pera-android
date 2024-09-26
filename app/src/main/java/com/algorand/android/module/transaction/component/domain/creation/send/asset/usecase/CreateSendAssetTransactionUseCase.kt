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

package com.algorand.android.module.transaction.component.domain.creation.send.asset.usecase

import com.algorand.android.module.algosdk.transaction.AlgoSdkTransaction
import com.algorand.android.module.algosdk.transaction.model.payload.AssetTransactionPayload
import com.algorand.android.module.transaction.component.domain.creation.CreateSendAssetTransaction
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.send.asset.model.CreateSendAssetTransactionPayload
import com.algorand.android.module.transaction.component.domain.mapper.SuggestedTransactionParamsMapper
import javax.inject.Inject

internal class CreateSendAssetTransactionUseCase @Inject constructor(
    private val algoSdkTransaction: AlgoSdkTransaction,
    private val suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper
) : CreateSendAssetTransaction {

    override suspend fun invoke(payload: CreateSendAssetTransactionPayload): CreateTransactionResult {
        val assetTxnPayload = payload.getAssetTransactionPayload()
        val suggestedParams = suggestedTransactionParamsMapper(payload.params)
        val assetTransaction = algoSdkTransaction.createAssetTransaction(assetTxnPayload, suggestedParams)
        return CreateTransactionResult.TransactionCreated(assetTransaction)
    }

    private fun CreateSendAssetTransactionPayload.getAssetTransactionPayload(): AssetTransactionPayload {
        return AssetTransactionPayload(
            senderAddress = senderAddress,
            receiverAddress = receiverAddress,
            amount = amount,
            assetId = assetId,
            noteInByteArray = note
        )
    }
}

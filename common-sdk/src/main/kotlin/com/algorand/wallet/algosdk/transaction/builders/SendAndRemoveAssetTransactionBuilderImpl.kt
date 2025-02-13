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

package com.algorand.wallet.algosdk.transaction.builders

import com.algorand.wallet.algosdk.transaction.model.Transaction
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.algosdk.transaction.sdk.model.SendAndRemoveAssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import javax.inject.Inject

internal class SendAndRemoveAssetTransactionBuilderImpl @Inject constructor(
    private val algoSdk: AlgoSdk
) : SendAndRemoveAssetTransactionBuilder {

    override fun invoke(
        payload: SendAndRemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.SendAndRemoveAssetTransaction {
        val txnByteArray = createTxnByteArray(payload, params)
        return Transaction.SendAndRemoveAssetTransaction(payload.senderAddress, txnByteArray)
    }

    private fun createTxnByteArray(
        payload: SendAndRemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): ByteArray {
        return with(payload) {
            algoSdk.createSendAndRemoveAssetTxn(
                senderAddress = senderAddress,
                receiverAddress = receiverAddress,
                assetId = assetId,
                amount = amount,
                noteInByteArray = noteInByteArray,
                suggestedTransactionParams = params
            )
        }
    }
}

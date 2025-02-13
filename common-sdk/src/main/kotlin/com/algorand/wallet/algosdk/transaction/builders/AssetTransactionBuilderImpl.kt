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
import com.algorand.wallet.algosdk.transaction.sdk.model.AssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import javax.inject.Inject

internal class AssetTransactionBuilderImpl @Inject constructor(
    private val algoSdk: AlgoSdk
) : AssetTransactionBuilder {

    override fun invoke(
        payload: AssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.AssetTransaction {
        val txnByteArray = createTxnByteArray(payload, params)
        return Transaction.AssetTransaction(payload.senderAddress, txnByteArray)
    }

    private fun createTxnByteArray(payload: AssetTransactionPayload, params: SuggestedTransactionParams): ByteArray {
        return with(payload) {
            algoSdk.createAssetTransferTxn(
                senderAddress = senderAddress,
                receiverAddress = receiverAddress,
                amount = amount,
                assetId = assetId,
                noteInByteArray = noteInByteArray,
                suggestedTransactionParams = params
            )
        }
    }
}

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

package com.algorand.android.algosdk.component.transaction.mapper.implementation

import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.algosdk.component.NumberExtensions.toUint64
import com.algorand.android.algosdk.component.transaction.mapper.AssetTransactionMapper
import com.algorand.android.algosdk.component.transaction.mapper.SuggestedParamsMapper
import com.algorand.android.algosdk.component.transaction.model.SuggestedTransactionParams
import com.algorand.android.algosdk.component.transaction.model.Transaction
import com.algorand.android.algosdk.component.transaction.model.payload.AssetTransactionPayload
import javax.inject.Inject

internal class AssetTransactionMapperImpl @Inject constructor(
    private val suggestedParamsMapper: SuggestedParamsMapper
) : AssetTransactionMapper {

    override fun invoke(
        payload: AssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.AssetTransaction {
        val txnByteArray = createTxnByteArray(payload, params)
        return Transaction.AssetTransaction(payload.senderAddress, txnByteArray)
    }

    private fun createTxnByteArray(payload: AssetTransactionPayload, params: SuggestedTransactionParams): ByteArray {
        return with(payload) {
            Sdk.makeAssetTransferTxn(
                senderAddress,
                receiverAddress,
                "",
                amount.toUint64(),
                noteInByteArray,
                suggestedParamsMapper(params, addGenesis = false),
                assetId
            )
        }
    }
}

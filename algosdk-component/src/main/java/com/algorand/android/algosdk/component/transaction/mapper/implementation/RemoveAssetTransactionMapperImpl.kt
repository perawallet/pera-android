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
import com.algorand.android.algosdk.component.transaction.mapper.RemoveAssetTransactionMapper
import com.algorand.android.algosdk.component.transaction.mapper.SuggestedParamsMapper
import com.algorand.android.algosdk.component.transaction.model.SuggestedTransactionParams
import com.algorand.android.algosdk.component.transaction.model.Transaction
import com.algorand.android.algosdk.component.transaction.model.payload.RemoveAssetTransactionPayload
import javax.inject.Inject

internal class RemoveAssetTransactionMapperImpl @Inject constructor(
    private val suggestedParamsMapper: SuggestedParamsMapper
) : RemoveAssetTransactionMapper {

    override fun invoke(
        payload: RemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.RemoveAssetTransaction {
        val txnByteArray = createTxnByteArray(payload, params)
        return Transaction.RemoveAssetTransaction(payload.senderAddress, txnByteArray)
    }

    private fun createTxnByteArray(
        payload: RemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): ByteArray {
        return with(payload) {
            Sdk.makeAssetTransferTxn(
                senderAddress,
                creatorAddress,
                creatorAddress,
                0L.toUint64(),
                null,
                suggestedParamsMapper(params, addGenesis = false),
                assetId
            )
        }
    }
}

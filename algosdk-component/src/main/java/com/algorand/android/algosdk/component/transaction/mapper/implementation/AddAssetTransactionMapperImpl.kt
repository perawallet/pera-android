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
import com.algorand.android.algosdk.component.transaction.mapper.AddAssetTransactionMapper
import com.algorand.android.algosdk.component.transaction.mapper.SuggestedParamsMapper
import com.algorand.android.algosdk.component.transaction.model.SuggestedTransactionParams
import com.algorand.android.algosdk.component.transaction.model.Transaction
import com.algorand.android.algosdk.component.transaction.model.payload.AddAssetTransactionPayload
import javax.inject.Inject

internal class AddAssetTransactionMapperImpl @Inject constructor(
    private val suggestedParamsMapper: SuggestedParamsMapper
) : AddAssetTransactionMapper {

    override fun invoke(
        payload: AddAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.AddAssetTransaction {
        val txnByteArray = createTxnByteArray(payload, params)
        return Transaction.AddAssetTransaction(payload.address, txnByteArray)
    }

    private fun createTxnByteArray(payload: AddAssetTransactionPayload, params: SuggestedTransactionParams): ByteArray {
        return Sdk.makeAssetAcceptanceTxn(
            payload.address,
            null,
            suggestedParamsMapper(params, addGenesis = true),
            payload.assetId
        )
    }
}

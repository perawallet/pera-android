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

package com.algorand.android.module.algosdk.transaction.mapper.implementation

import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.module.algosdk.NumberExtensions.toUint64
import com.algorand.android.module.algosdk.transaction.mapper.AlgoTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.SuggestedParamsMapper
import com.algorand.android.module.algosdk.transaction.model.SuggestedTransactionParams
import com.algorand.android.module.algosdk.transaction.model.Transaction
import com.algorand.android.module.algosdk.transaction.model.payload.AlgoTransactionPayload
import javax.inject.Inject

internal class AlgoTransactionMapperImpl @Inject constructor(
    private val suggestedParamsMapper: SuggestedParamsMapper
) : AlgoTransactionMapper {

    override fun invoke(
        payload: AlgoTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.AlgoTransaction {
        val txnByteArray = createTxnByteArray(payload, params)
        return Transaction.AlgoTransaction(payload.senderAddress, txnByteArray)
    }

    private fun createTxnByteArray(payload: AlgoTransactionPayload, params: SuggestedTransactionParams): ByteArray {
        return with(payload) {
            Sdk.makePaymentTxn(
                senderAddress,
                receiverAddress,
                amount.toUint64(),
                noteInByteArray,
                if (isMaxAmount) receiverAddress else "",
                suggestedParamsMapper(params, addGenesis = true)
            )
        }
    }
}

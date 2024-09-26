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
import com.algorand.android.module.algosdk.transaction.mapper.RekeyTransactionMapper
import com.algorand.android.module.algosdk.transaction.mapper.SuggestedParamsMapper
import com.algorand.android.module.algosdk.transaction.model.SuggestedTransactionParams
import com.algorand.android.module.algosdk.transaction.model.Transaction
import com.algorand.android.module.algosdk.transaction.model.payload.RekeyTransactionPayload
import javax.inject.Inject

internal class RekeyTransactionMapperImpl @Inject constructor(
    private val suggestedParamsMapper: SuggestedParamsMapper
) : RekeyTransactionMapper {

    override fun invoke(
        payload: RekeyTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.RekeyTransaction {
        val txnByteArray = createTxnByteArray(payload, params)
        return Transaction.RekeyTransaction(payload.address, txnByteArray)
    }

    private fun createTxnByteArray(payload: RekeyTransactionPayload, params: SuggestedTransactionParams): ByteArray {
        return Sdk.makeRekeyTxn(
            payload.address,
            payload.rekeyAdminAddress,
            suggestedParamsMapper(params, addGenesis = true)
        )
    }
}
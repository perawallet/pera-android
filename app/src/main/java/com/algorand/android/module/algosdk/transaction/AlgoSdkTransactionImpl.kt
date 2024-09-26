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

package com.algorand.android.module.algosdk.transaction

import com.algorand.algosdk.v2.client.Utils
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.android.module.algosdk.transaction.mapper.TransactionMappers
import com.algorand.android.module.algosdk.transaction.model.SuggestedTransactionParams
import com.algorand.android.module.algosdk.transaction.model.Transaction
import com.algorand.android.module.algosdk.transaction.model.payload.AddAssetTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.AlgoTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.AssetTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.RekeyTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.RemoveAssetTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.SendAndRemoveAssetTransactionPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AlgoSdkTransactionImpl(
    private val transactionMappers: TransactionMappers,
    private val algodClient: AlgodClient
) : AlgoSdkTransaction {

    override suspend fun waitForConfirmation(txnId: String, maxRoundToWait: Int): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val transactionResponse = Utils.waitForConfirmation(algodClient, txnId, maxRoundToWait)
                Result.success(transactionResponse.txn.transactionID)
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun createAssetTransaction(
        payload: AssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.AssetTransaction {
        return transactionMappers.assetTransactionMapper(payload, params)
    }

    override fun createAlgoTransaction(
        payload: AlgoTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.AlgoTransaction {
        return transactionMappers.algoTransactionMapper(payload, params)
    }

    override fun createRekeyTransaction(
        payload: RekeyTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.RekeyTransaction {
        return transactionMappers.rekeyTransactionMapper(payload, params)
    }

    override fun createAddAssetTransaction(
        payload: AddAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.AddAssetTransaction {
        return transactionMappers.addAssetTransactionMapper(payload, params)
    }

    override fun createRemoveAssetTransaction(
        payload: RemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.RemoveAssetTransaction {
        return transactionMappers.removeAssetTransactionMapper(payload, params)
    }

    override fun createSendAndRemoveAssetTransaction(
        payload: SendAndRemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): Transaction.SendAndRemoveAssetTransaction {
        return transactionMappers.sendAndRemoveAssetTransactionMapper(payload, params)
    }
}

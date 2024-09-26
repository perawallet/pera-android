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

import com.algorand.android.module.algosdk.transaction.model.SuggestedTransactionParams
import com.algorand.android.module.algosdk.transaction.model.Transaction.AddAssetTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.AlgoTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.AssetTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.RekeyTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.RemoveAssetTransaction
import com.algorand.android.module.algosdk.transaction.model.Transaction.SendAndRemoveAssetTransaction
import com.algorand.android.module.algosdk.transaction.model.payload.AddAssetTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.AlgoTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.AssetTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.RekeyTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.RemoveAssetTransactionPayload
import com.algorand.android.module.algosdk.transaction.model.payload.SendAndRemoveAssetTransactionPayload

interface AlgoSdkTransaction {

    suspend fun waitForConfirmation(txnId: String, maxRoundToWait: Int): Result<String>

    fun createAssetTransaction(payload: AssetTransactionPayload, params: SuggestedTransactionParams): AssetTransaction

    fun createAlgoTransaction(payload: AlgoTransactionPayload, params: SuggestedTransactionParams): AlgoTransaction

    fun createRekeyTransaction(payload: RekeyTransactionPayload, params: SuggestedTransactionParams): RekeyTransaction

    fun createAddAssetTransaction(
        payload: AddAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): AddAssetTransaction

    fun createRemoveAssetTransaction(
        payload: RemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): RemoveAssetTransaction

    fun createSendAndRemoveAssetTransaction(
        payload: SendAndRemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): SendAndRemoveAssetTransaction
}

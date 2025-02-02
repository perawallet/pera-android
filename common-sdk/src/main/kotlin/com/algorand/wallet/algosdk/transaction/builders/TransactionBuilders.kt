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

import com.algorand.wallet.algosdk.transaction.model.Transaction.AddAssetTransaction
import com.algorand.wallet.algosdk.transaction.model.Transaction.AlgoTransaction
import com.algorand.wallet.algosdk.transaction.model.Transaction.AssetTransaction
import com.algorand.wallet.algosdk.transaction.model.Transaction.RekeyTransaction
import com.algorand.wallet.algosdk.transaction.model.Transaction.RemoveAssetTransaction
import com.algorand.wallet.algosdk.transaction.model.Transaction.SendAndRemoveAssetTransaction
import com.algorand.wallet.algosdk.transaction.sdk.model.AddAssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.AlgoTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.AssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.RekeyTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.RemoveAssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SendAndRemoveAssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams

internal interface AddAssetTransactionBuilder {
    operator fun invoke(payload: AddAssetTransactionPayload, params: SuggestedTransactionParams): AddAssetTransaction
}

internal interface AssetTransactionBuilder {
    operator fun invoke(payload: AssetTransactionPayload, params: SuggestedTransactionParams): AssetTransaction
}

internal interface AlgoTransactionBuilder {
    operator fun invoke(payload: AlgoTransactionPayload, params: SuggestedTransactionParams): AlgoTransaction
}

internal interface RekeyTransactionBuilder {
    operator fun invoke(payload: RekeyTransactionPayload, params: SuggestedTransactionParams): RekeyTransaction
}

internal interface RemoveAssetTransactionBuilder {
    operator fun invoke(
        payload: RemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): RemoveAssetTransaction
}

internal interface SendAndRemoveAssetTransactionBuilder {
    operator fun invoke(
        payload: SendAndRemoveAssetTransactionPayload,
        params: SuggestedTransactionParams
    ): SendAndRemoveAssetTransaction
}

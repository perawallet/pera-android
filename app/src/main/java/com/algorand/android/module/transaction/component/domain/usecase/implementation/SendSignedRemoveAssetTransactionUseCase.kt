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

package com.algorand.android.module.transaction.component.domain.usecase.implementation

import com.algorand.android.module.account.info.domain.usecase.AddAssetRemovalToAccountAssetHoldings
import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.transaction.component.domain.model.SendSignedRemoveAssetTransactionPayload
import com.algorand.android.module.transaction.component.domain.model.TransactionId
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedRemoveAssetTransaction
import com.algorand.android.module.transaction.component.domain.usecase.SendSignedTransaction
import javax.inject.Inject

internal class SendSignedRemoveAssetTransactionUseCase @Inject constructor(
    private val sendSignedTransaction: SendSignedTransaction,
    private val addAssetRemovalToAccountAssetHoldings: AddAssetRemovalToAccountAssetHoldings
) : SendSignedRemoveAssetTransaction {

    override suspend fun invoke(payload: SendSignedRemoveAssetTransactionPayload): PeraResult<TransactionId> {
        return sendSignedTransaction(payload.transaction, payload.waitForConfirmation).also {
            if (it.isSuccess) {
                addAssetRemovalToAccountAssetHoldings(payload.address, payload.assetId)
            }
        }
    }
}

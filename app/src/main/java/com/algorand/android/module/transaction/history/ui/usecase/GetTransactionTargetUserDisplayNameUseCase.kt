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

package com.algorand.android.module.transaction.history.ui.usecase

import com.algorand.android.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.transaction.pendingtxn.domain.model.PendingTransaction
import com.algorand.android.transaction_history_component.domain.model.BaseTransactionHistoryItem
import javax.inject.Inject

internal class GetTransactionTargetUserDisplayNameUseCase @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName
) : GetTransactionTargetUserDisplayName {

    override suspend fun invoke(
        transaction: BaseTransactionHistoryItem.BaseTransactionHistory,
        address: String
    ): String? {
        with(transaction) {
            return if (receiverAddress == address && address == senderAddress) {
                null
            } else {
                val otherPublicKey = if (receiverAddress == address) senderAddress else receiverAddress
                otherPublicKey?.let { getAccountDisplayName(otherPublicKey).primaryDisplayName }
            }
        }
    }

    override suspend fun invoke(pendingTransaction: PendingTransaction, address: String): String? {
        with(pendingTransaction) {
            return if (getReceiverAddress() == address && address == getSenderAddress()) {
                null
            } else {
                val otherPublicKey = if (getReceiverAddress() == address) getSenderAddress() else getReceiverAddress()
                if (otherPublicKey.isBlank()) return null
                otherPublicKey.let { getAccountDisplayName(otherPublicKey).primaryDisplayName }
            }
        }
    }
}

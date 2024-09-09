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

package com.algorand.android.transactionhistoryui.pendingtxn.domain.usecase

import com.algorand.android.foundation.PeraResult
import com.algorand.android.transaction.pendingtxn.domain.usecase.GetPendingTransactions
import com.algorand.android.transactionhistoryui.model.BaseTransactionItem
import com.algorand.android.transactionhistoryui.model.BaseTransactionItem.TransactionItem
import com.algorand.android.transactionhistoryui.pendingtxn.domain.mapper.PendingTransactionItemMapper
import javax.inject.Inject

internal class GetPendingTransactionItemsUseCase @Inject constructor(
    private val getPendingTransaction: GetPendingTransactions,
    private val pendingTransactionItemMapper: PendingTransactionItemMapper
) : GetPendingTransactionItems {

    override val pendingFlowDistinctUntilChangedListener: (
        oldTransactions: List<BaseTransactionItem>?,
        newTransactions: List<BaseTransactionItem>?
    ) -> Boolean
        get() = getPendingFlowListener()

    override suspend fun invoke(address: String, assetId: Long?): PeraResult<List<BaseTransactionItem>> {
        return getPendingTransaction(address, assetId).map { pendingTransactions ->
            pendingTransactions.map { pendingTransaction ->
                pendingTransactionItemMapper(pendingTransaction, address)
            }
        }
    }

    private fun getPendingFlowListener(): (
        oldTransactions: List<BaseTransactionItem>?,
        newTransactions: List<BaseTransactionItem>?
    ) -> Boolean {
        return { oldTransactions, newTransactions ->
            newTransactions?.filterIsInstance<TransactionItem>()?.any { new ->
                oldTransactions?.filterIsInstance<TransactionItem>()?.any { old ->
                    old.isSameTransaction(new)
                } == true
            } == true
        }
    }
}

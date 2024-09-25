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

package com.algorand.android.module.swap.ui.confirmswap

import androidx.lifecycle.Lifecycle
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.common.ListQueuingHelper
import com.algorand.android.foundation.common.ListQueuingHelper.Listener
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SignedSwapSingleTransactionData
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.UnsignedSwapSingleTransactionData
import com.algorand.android.module.swap.ui.confirmswap.mapper.SignSwapTransactionResultMapper
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult
import com.algorand.android.module.swap.ui.confirmswap.model.SignSwapTransactionResult.TransactionsSigned
import com.algorand.android.module.transaction.component.domain.model.SignedTransaction
import com.algorand.android.module.transaction.component.domain.sign.SignTransactionManager
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class SignSwapTransactionManagerImpl @Inject constructor(
    private val signTransactionManager: SignTransactionManager,
    private val signSwapTransactionResultMapper: SignSwapTransactionResultMapper
) : SignSwapTransactionManager {

    private var swapQuoteTransaction: List<SwapQuoteTransaction>? = null
    private var currentUnsignedSwapSingleTransactionData: UnsignedSwapSingleTransactionData? = null

    private val swapTransactionSignHelper = ListQueuingHelper<UnsignedSwapSingleTransactionData, ByteArray>()

    private val _signSwapTransactionResultFlow = MutableStateFlow<Event<SignSwapTransactionResult>?>(null)
    override val signSwapTransactionResultFlow: Flow<Event<SignSwapTransactionResult>?>
        get() = _signSwapTransactionResultFlow.asStateFlow()

    private val signHelperListener = object : Listener<UnsignedSwapSingleTransactionData, ByteArray> {
        override fun onAllItemsDequeued(dequeuedItemList: List<ByteArray?>) {
            swapQuoteTransaction?.let {
                _signSwapTransactionResultFlow.value = Event(TransactionsSigned(it))
            }
        }

        override fun onNextItemToBeDequeued(
            item: UnsignedSwapSingleTransactionData,
            currentItemIndex: Int,
            totalItemCount: Int
        ) {
            val transaction = item.transactionByteArray
            if (transaction == null) {
                swapTransactionSignHelper.cacheDequeuedItem(null)
                return
            }
            currentUnsignedSwapSingleTransactionData = item
            signTransactionManager.sign(item.getSignerAddress(), transaction)
        }
    }

    override fun setup(lifecycle: Lifecycle) {
        signTransactionManager.setup(lifecycle)
    }

    override suspend fun sign(transactions: List<SwapQuoteTransaction>) {
        coroutineScope {
            launch { initSignManagerResultFlowCollector() }
            swapQuoteTransaction = transactions
            swapTransactionSignHelper.initListener(signHelperListener)
            val unsignedTransactions = transactions.map { it.getTransactionsThatNeedsToBeSigned() }.flatten()
            swapTransactionSignHelper.initItemsToBeEnqueued(unsignedTransactions)
        }
    }

    override fun stopAllResources() {
        signTransactionManager.stopAllResources()
    }

    override fun clearCachedTransactions() {
        swapQuoteTransaction = null
        currentUnsignedSwapSingleTransactionData = null
    }

    override fun signCachedTransaction() {
        currentUnsignedSwapSingleTransactionData?.let { txn ->
            if (txn.transactionByteArray != null) {
                signTransactionManager.sign(txn.getSignerAddress(), txn.transactionByteArray!!)
            }
        }
    }

    private suspend fun initSignManagerResultFlowCollector() {
        signTransactionManager.signTransactionResultFlow.collectLatest {
            val result = it?.consume() ?: return@collectLatest
            if (result is SignTransactionResult.TransactionSigned) {
                onTransactionSigned(result.signedTransaction)
            } else {
                val signSwapTxnResult = signSwapTransactionResultMapper(result) ?: return@collectLatest
                _signSwapTransactionResultFlow.value = Event(signSwapTxnResult)
            }
        }
    }

    private fun onTransactionSigned(transaction: SignedTransaction) {
        currentUnsignedSwapSingleTransactionData?.run {
            val signedSingleTransactionData = SignedSwapSingleTransactionData(
                parentListIndex,
                transactionListIndex,
                transaction.value
            )
            swapQuoteTransaction?.get(signedSingleTransactionData.parentListIndex)?.insertSignedTransaction(
                transactionListIndex,
                signedSingleTransactionData
            )
            swapTransactionSignHelper.cacheDequeuedItem(transaction.value)
        }
    }
}

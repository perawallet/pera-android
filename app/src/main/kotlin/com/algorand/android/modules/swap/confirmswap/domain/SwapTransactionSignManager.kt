/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.swap.confirmswap.domain

import android.util.Log
import com.algorand.android.core.transaction.external.ExternalTransactionSignManager
import com.algorand.android.core.transaction.sync.JointAccountSyncSignDependencies
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.ledger.operations.ExternalTransaction
import com.algorand.android.modules.swap.confirmswap.domain.model.SignedSwapSingleTransactionData
import com.algorand.android.modules.swap.confirmswap.domain.model.SwapQuoteTransaction
import com.algorand.android.modules.swap.confirmswap.domain.model.UnsignedSwapSingleTransactionData
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionQueuingHelper
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.utils.decodeBase64
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SwapTransactionSignManager @Inject constructor(
    ledgerBleSearchManager: LedgerBleSearchManager,
    ledgerBleOperationManager: LedgerBleOperationManager,
    externalTransactionQueuingHelper: ExternalTransactionQueuingHelper,
    getTransactionSigner: GetTransactionSigner,
    getAlgo25SecretKey: GetAlgo25SecretKey,
    getHdSeed: GetHdSeed,
    getLocalAccount: GetLocalAccount,
    signHdKeyTransaction: SignHdKeyTransaction,
    syncSignDependencies: JointAccountSyncSignDependencies
) : ExternalTransactionSignManager<UnsignedSwapSingleTransactionData>(
    ledgerBleSearchManager,
    ledgerBleOperationManager,
    externalTransactionQueuingHelper,
    getTransactionSigner,
    getAlgo25SecretKey,
    getHdSeed,
    getLocalAccount,
    signHdKeyTransaction,
    syncSignDependencies
) {

    val swapTransactionSignResultFlow: Flow<ExternalTransactionSignResult> = signResultFlow.map {
        when (it) {
            is ExternalTransactionSignResult.Success<*> -> {
                swapQuoteTransaction?.run {
                    ExternalTransactionSignResult.Success(this)
                } ?: it
            }

            else -> it
        }
    }

    private var swapQuoteTransaction: List<SwapQuoteTransaction>? = null

    fun signSwapQuoteTransaction(swapQuoteTransaction: List<SwapQuoteTransaction>) {
        this.swapQuoteTransaction = swapQuoteTransaction
        val unsignedTransactionList = swapQuoteTransaction.map { it.getTransactionsThatNeedsToBeSigned() }.flatten()
        Log.d(
            TAG,
            "signSwapQuote: groups=${swapQuoteTransaction.size}, " +
                "totalUnsigned=${unsignedTransactionList.size}, " +
                "allTxnCounts=${swapQuoteTransaction.map { it.unsignedTransactions.size }}, " +
                "needSignCounts=${swapQuoteTransaction.map { it.getTransactionsThatNeedsToBeSigned().size }}"
        )
        signTransaction(unsignedTransactionList)
    }

    override fun onTransactionSigned(transaction: ExternalTransaction, signedTransaction: ByteArray?) {
        insertSignedSwapTransaction(transaction, signedTransaction)
        super.onTransactionSigned(transaction, signedTransaction)
    }

    override fun buildRawBytesGroups(
        txList: List<UnsignedSwapSingleTransactionData>
    ): List<List<ByteArray>>? {
        val quoteTransactions = swapQuoteTransaction
        if (quoteTransactions == null) {
            Log.e(TAG, "buildRawBytesGroups: swapQuoteTransaction is null")
            return null
        }
        Log.d(TAG, "buildRawBytesGroups: quoteGroups=${quoteTransactions.size}")
        val result = mutableListOf<List<ByteArray>>()
        for ((gIdx, group) in quoteTransactions.withIndex()) {
            val allTxns = group.unsignedTransactions
            Log.d(
                TAG,
                "buildRawBytesGroups: group[$gIdx] allTxns=${allTxns.size}, " +
                    "hasMsgPack=${allTxns.map { it.transactionMsgPack != null }}"
            )
            val allRawBytes = mutableListOf<ByteArray>()
            for ((tIdx, txData) in allTxns.withIndex()) {
                val bytes = txData.transactionMsgPack?.decodeBase64()
                if (bytes == null) {
                    Log.e(TAG, "buildRawBytesGroups: FAIL group[$gIdx] txn[$tIdx] decode null")
                    return null
                }
                allRawBytes.add(bytes)
            }
            if (allRawBytes.isEmpty()) {
                Log.e(TAG, "buildRawBytesGroups: group[$gIdx] empty after decode")
                continue
            }
            result.add(allRawBytes)
        }
        if (result.isEmpty()) {
            Log.e(TAG, "buildRawBytesGroups: no groups produced")
            return null
        }
        Log.d(TAG, "buildRawBytesGroups: result groups=${result.map { it.size }}")
        return result
    }

    override fun onJointSignedTransactionsAssembled(
        transactions: List<UnsignedSwapSingleTransactionData>,
        signedBytesList: List<ByteArray>
    ) {
        transactions.forEachIndexed { index, transaction ->
            insertSignedSwapTransaction(transaction, signedBytesList[index])
        }
    }

    private fun insertSignedSwapTransaction(transaction: ExternalTransaction, signedTransaction: ByteArray?) {
        (transaction as? UnsignedSwapSingleTransactionData)?.run {
            val signedSingleTransactionData = SignedSwapSingleTransactionData(
                parentListIndex,
                transactionListIndex,
                signedTransaction
            )
            swapQuoteTransaction?.get(signedSingleTransactionData.parentListIndex)?.insertSignedTransaction(
                transactionListIndex,
                signedSingleTransactionData
            )
        }
    }

    companion object {
        private const val TAG = "JOINT_SIGN_DEBUG"
    }
}

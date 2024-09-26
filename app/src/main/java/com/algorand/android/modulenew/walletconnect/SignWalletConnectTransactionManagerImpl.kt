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

package com.algorand.android.modulenew.walletconnect

import androidx.lifecycle.Lifecycle
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountsDetail
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.coroutine.LifecycleScopedCoroutineOwner
import com.algorand.android.models.BaseWalletConnectTransaction
import com.algorand.android.models.WalletConnectRequest.WalletConnectTransaction
import com.algorand.android.models.WalletConnectSignResult.CanBeSigned
import com.algorand.android.models.WalletConnectSignResult.Error
import com.algorand.android.module.transaction.component.domain.sign.SignTransactionManager
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import com.algorand.android.utils.ListQueuingHelper.Listener
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.walletconnect.WalletConnectSignValidator
import com.algorand.android.utils.walletconnect.WalletConnectTransactionSignHelper
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class SignWalletConnectTransactionManagerImpl @Inject constructor(
    private val signTransactionManager: SignTransactionManager,
    private val signWalletConnectTransactionResultMapper: SignWalletConnectTransactionResultMapper,
    private val walletConnectSignValidator: WalletConnectSignValidator,
    private val getAccountsDetail: GetAccountsDetail
) : LifecycleScopedCoroutineOwner(), SignWalletConnectTransactionManager {

    private val _signResultFlow = MutableStateFlow<Event<SignWalletConnectTransactionResult>?>(null)
    override val signWalletConnectTransactionResultFlow: Flow<Event<SignWalletConnectTransactionResult>?>
        get() = _signResultFlow.asStateFlow()

    private val walletConnectSignHelper = WalletConnectTransactionSignHelper()

    private var wcTransaction: WalletConnectTransaction? = null
    private var currentUnsignedWcTxn: BaseWalletConnectTransaction? = null

    private val walletConnectSignHelperListener = object : Listener<BaseWalletConnectTransaction, ByteArray> {
        override fun onAllItemsDequeued(dequeuedItemList: List<ByteArray?>) {
            wcTransaction?.run {
                val successResult = SignWalletConnectTransactionResult.TransactionsSigned(
                    transactions = dequeuedItemList,
                    sessionIdentifier = session.sessionIdentifier,
                    requestId = requestId
                )
                _signResultFlow.value = Event(successResult)
            }
        }

        override fun onNextItemToBeDequeued(
            item: BaseWalletConnectTransaction,
            currentItemIndex: Int,
            totalItemCount: Int
        ) {
            signSingleTransaction(item)
        }
    }

    override fun setup(lifecycle: Lifecycle) {
        assignToLifecycle(lifecycle)
        signTransactionManager.setup(lifecycle)
    }

    override fun sign(transaction: WalletConnectTransaction) {
        when (val result = walletConnectSignValidator.canTransactionBeSigned(transaction)) {
            is CanBeSigned -> signWalletConnectTransaction(transaction)
            is Error -> _signResultFlow.value = Event(signWalletConnectTransactionResultMapper(result))
            else -> {
                sendErrorLog("Unhandled else case in WalletConnectSignManager.signTransaction")
            }
        }
    }

    private fun signWalletConnectTransaction(transaction: WalletConnectTransaction) {
        currentScope.launch {
            launch { initSignManagerResultFlowCollector() }
            wcTransaction = transaction
            walletConnectSignHelper.initListener(walletConnectSignHelperListener)
            walletConnectSignHelper.initItemsToBeEnqueued(transaction.transactionList.flatten())
        }
    }

    override fun stopAllResources() {
        signTransactionManager.stopAllResources()
    }

    override fun clearCachedTransactions() {
        wcTransaction = null
        currentUnsignedWcTxn = null
    }

    override fun signCachedTransaction() {
        currentUnsignedWcTxn?.let {
            signSingleTransaction(it)
        }
    }

    private fun signSingleTransaction(transaction: BaseWalletConnectTransaction) {
        currentScope.launch {
            val accountDetails = getAccountsDetail()
            val signerDetail = accountDetails.firstOrNull { it.address == transaction.signer.address?.decodedAddress }
            val decodedTxn = transaction.decodedTransaction
            if (signerDetail == null || signerDetail.accountType?.canSignTransaction() == false || decodedTxn == null) {
                walletConnectSignHelper.cacheDequeuedItem(null)
            } else {
                currentUnsignedWcTxn = transaction
                signTransactionManager.sign(signerDetail.address, decodedTxn)
            }
        }
    }

    private suspend fun initSignManagerResultFlowCollector() {
        signTransactionManager.signTransactionResultFlow.collectLatest {
            val result = it?.consume() ?: return@collectLatest
            if (result is SignTransactionResult.TransactionSigned) {
                walletConnectSignHelper.cacheDequeuedItem(result.signedTransaction.value)
            } else {
                val signWcTxnResult = signWalletConnectTransactionResultMapper(result) ?: return@collectLatest
                _signResultFlow.value = Event(signWcTxnResult)
            }
        }
    }
}

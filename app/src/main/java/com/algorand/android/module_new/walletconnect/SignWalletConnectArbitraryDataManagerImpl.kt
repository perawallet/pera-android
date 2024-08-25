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

package com.algorand.android.module_new.walletconnect

import androidx.lifecycle.Lifecycle
import com.algorand.android.account.localaccount.domain.usecase.GetSecretKey
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.coroutine.LifecycleScopedCoroutineOwner
import com.algorand.android.models.WalletConnectArbitraryData
import com.algorand.android.models.WalletConnectRequest.WalletConnectArbitraryDataRequest
import com.algorand.android.models.WalletConnectSignResult.CanBeSigned
import com.algorand.android.models.WalletConnectSignResult.Error
import com.algorand.android.module_new.walletconnect.SignWalletConnectArbitraryDataResult.Error.Defined
import com.algorand.android.module_new.walletconnect.SignWalletConnectArbitraryDataResult.TransactionsSigned
import com.algorand.android.utils.ListQueuingHelper
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.signArbitraryData
import com.algorand.android.utils.walletconnect.WalletConnectArbitraryDataSignHelper
import com.algorand.android.utils.walletconnect.WalletConnectSignValidator
import javax.inject.Inject
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class SignWalletConnectArbitraryDataManagerImpl @Inject constructor(
    private val walletConnectSignValidator: WalletConnectSignValidator,
    private val getSecretKey: GetSecretKey,
    private val signHelper: WalletConnectArbitraryDataSignHelper,
) : LifecycleScopedCoroutineOwner(), SignWalletConnectArbitraryDataManager {

    private val _signResultFlow = MutableStateFlow<Event<SignWalletConnectArbitraryDataResult>?>(null)
    override val signWalletConnectTransactionResultFlow: Flow<Event<SignWalletConnectArbitraryDataResult>?>
        get() = _signResultFlow

    private var arbitraryDataTransaction: WalletConnectArbitraryDataRequest? = null

    private val signHelperListener = object : ListQueuingHelper.Listener<WalletConnectArbitraryData, ByteArray> {
        override fun onAllItemsDequeued(dequeuedItemList: List<ByteArray?>) {
            arbitraryDataTransaction?.run {
                val success = TransactionsSigned(dequeuedItemList, session.sessionIdentifier, requestId)
                _signResultFlow.value = Event(success)
            }
        }

        override fun onNextItemToBeDequeued(
            item: WalletConnectArbitraryData,
            currentItemIndex: Int,
            totalItemCount: Int
        ) {
            currentScope.launch {
                val secretKey = getSecretKey(item.signerAccount?.address.orEmpty())
                if (secretKey != null) {
                    signHelper.cacheDequeuedItem(item.decodedTransaction?.signArbitraryData(secretKey))
                } else {
                    signHelper.cacheDequeuedItem(null)
                }
            }
        }
    }

    override fun setup(lifecycle: Lifecycle) {
        assignToLifecycle(lifecycle)
        signHelper.initListener(signHelperListener)
    }

    override fun sign(transaction: WalletConnectArbitraryDataRequest) {
        when (val result = walletConnectSignValidator.canArbitraryDataBeSigned(transaction)) {
            is CanBeSigned -> signWalletConnectTransaction(transaction)
            is Error.Defined -> _signResultFlow.value = Event(Defined(result.description, result.titleResId))
            else -> {
                sendErrorLog("Unhandled else case in WalletConnectSignManager.signTransaction")
            }
        }
    }

    private fun signWalletConnectTransaction(transaction: WalletConnectArbitraryDataRequest) {
        currentScope.launch {
            arbitraryDataTransaction = transaction
            signHelper.initItemsToBeEnqueued(transaction.arbitraryDataList)
        }
    }

    override fun stopAllResources() {
        currentScope.coroutineContext.cancelChildren()
        signHelper.clearCachedData()
        arbitraryDataTransaction = null
    }

    override fun clearCachedTransactions() {
        arbitraryDataTransaction = null
        signHelper.clearCachedData()
    }
}

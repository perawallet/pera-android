/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.utils.walletconnect

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.algorand.android.models.WalletConnectArbitraryData
import com.algorand.android.models.WalletConnectRequest.WalletConnectArbitraryDataRequest
import com.algorand.android.models.WalletConnectSignResult
import com.algorand.android.models.WalletConnectSignResult.Success
import com.algorand.android.utils.LifecycleScopedCoroutineOwner
import com.algorand.android.utils.ListQueuingHelper
import com.algorand.android.utils.sendErrorLog
import com.algorand.android.utils.signArbitraryData
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import javax.inject.Inject
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class WalletConnectArbitraryDataSignManager @Inject constructor(
    private val walletConnectSignValidator: WalletConnectSignValidator,
    private val signHelper: WalletConnectArbitraryDataSignHelper,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val getLocalAccount: GetLocalAccount,
    private val signHdKeyTransaction: SignHdKeyTransaction
) : LifecycleScopedCoroutineOwner() {

    val signResultLiveData: LiveData<WalletConnectSignResult>
        get() = _signResultLiveData
    private val _signResultLiveData = MutableLiveData<WalletConnectSignResult>()

    private var arbitraryData: WalletConnectArbitraryDataRequest? = null

    private val signHelperListener = object : ListQueuingHelper.Listener<WalletConnectArbitraryData, ByteArray> {
        override fun onAllItemsDequeued(signedTransactions: List<ByteArray?>) {
            arbitraryData?.run {
                _signResultLiveData.postValue(
                    Success(
                        session.sessionIdentifier,
                        requestId,
                        signedTransactions
                    )
                )
            }
        }

        override fun onNextItemToBeDequeued(
            item: WalletConnectArbitraryData,
            currentItemIndex: Int,
            totalItemCount: Int
        ) {
            currentScope.launch {
                val signerAddress = item.signerAccount?.address

                if (signerAddress.isNullOrBlank()) {
                    cacheNullDequeuedItem()
                    return@launch
                }

                getLocalAccount(signerAddress).let { localAccount ->
                    when (localAccount) {
                        is LocalAccount.Algo25 -> handleAlgo25Signing(item, signerAddress)
                        is LocalAccount.HdKey -> handleHdKeySigning(item, localAccount)
                        else -> cacheNullDequeuedItem()
                    }
                }
            }
        }

        private suspend fun handleAlgo25Signing(
            item: WalletConnectArbitraryData,
            signerAddress: String
        ) {
            val secretKey = getAlgo25SecretKey(signerAddress)
            item.signArbitraryData(secretKey)
        }

        private suspend fun handleHdKeySigning(
            item: WalletConnectArbitraryData,
            localAccount: LocalAccount.HdKey
        ) {
            val transactionBytes = item.decodedTransaction ?: return cacheNullDequeuedItem()

            val seed = getHdSeed(seedId = localAccount.seedId) ?: return cacheNullDequeuedItem()

            val transactionSignedByteArray = signHdKeyTransaction.signTransaction(
                transactionBytes,
                seed,
                localAccount.account,
                localAccount.change,
                localAccount.keyIndex
            ) ?: return cacheNullDequeuedItem()

            signHelper.cacheDequeuedItem(transactionSignedByteArray)
        }

        private fun cacheNullDequeuedItem() {
            signHelper.cacheDequeuedItem(null)
        }
    }

    fun setup(lifecycle: Lifecycle) {
        assignToLifecycle(lifecycle)
        signHelper.initListener(signHelperListener)
    }

    fun signArbitraryData(arbitraryData: WalletConnectArbitraryDataRequest) {
        postResult(WalletConnectSignResult.Loading)
        this.arbitraryData = arbitraryData
        with(arbitraryData) {
            when (val result = walletConnectSignValidator.canArbitraryDataBeSigned(this)) {
                is WalletConnectSignResult.CanBeSigned -> {
                    signHelper.initItemsToBeEnqueued(arbitraryDataList)
                }

                is WalletConnectSignResult.Error -> postResult(result)
                else -> {
                    sendErrorLog("Unhandled else case in WalletConnectSignManager.signTransaction")
                }
            }
        }
    }

    private fun WalletConnectArbitraryData.signArbitraryData(secretKey: ByteArray?) {
        if (secretKey != null) {
            signHelper.cacheDequeuedItem(decodedTransaction?.signArbitraryData(secretKey))
        } else {
            signHelper.cacheDequeuedItem(null)
        }
    }

    private fun postResult(walletConnectSignResult: WalletConnectSignResult) {
        _signResultLiveData.postValue(walletConnectSignResult)
    }

    override fun stopAllResources() {
        signHelper.clearCachedData()
        arbitraryData = null
    }

    fun manualStopAllResources() {
        this.stopAllResources()
        currentScope.coroutineContext.cancelChildren()
    }
}

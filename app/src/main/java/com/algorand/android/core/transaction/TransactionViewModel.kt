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

package com.algorand.android.core.transaction

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.algosdk.component.transaction.model.Transaction
import com.algorand.android.foundation.Event
import com.algorand.android.module.transaction.component.domain.sign.SignTransactionManager
import com.algorand.android.module.transaction.ui.core.mapper.SignTransactionUiResultMapper
import com.algorand.android.module.transaction.ui.core.model.SignTransactionUiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val signTransactionManager: SignTransactionManager,
    private val signTransactionUiResultMapper: SignTransactionUiResultMapper
) : ViewModel() {

    private val _signTransactionUiResultFlow = MutableStateFlow<Event<SignTransactionUiResult>?>(null)
    val signTransactionUiResultFlow: Flow<Event<SignTransactionUiResult>?>
        get() = _signTransactionUiResultFlow.asStateFlow()

    private var bleWaitingTransactionData: Transaction? = null

    init {
        viewModelScope.launch {
            signTransactionManager.signTransactionResultFlow.collectLatest {
                it?.consume()?.let {
                    val signTransactionUiResult = signTransactionUiResultMapper(it)
                    _signTransactionUiResultFlow.value = Event(signTransactionUiResult)
                }
            }
        }
    }

    fun setup(lifecycle: Lifecycle) {
        signTransactionManager.setup(lifecycle)
    }

    fun processTransaction(transaction: Transaction) {
        bleWaitingTransactionData = transaction
        signTransactionManager.sign(transaction.signerAddress, transaction.value)
    }

    fun stopAllResources() {
        signTransactionManager.stopAllResources()
    }

    fun processWaitingTransaction() {
        bleWaitingTransactionData?.run {
            processTransaction(this)
        }
    }

    fun clearCachedTransactions() {
        bleWaitingTransactionData = null
    }
}

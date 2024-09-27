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

package com.algorand.android.module.transaction.component.domain.sign.usecase

import androidx.lifecycle.Lifecycle
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.transaction.component.domain.sign.SignTransactionManager
import com.algorand.android.module.transaction.component.domain.sign.mapper.SignTransactionNotReadyToSignToSignTransactionResultMapper
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionPreparednessResult.NotReadyToSign
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionPreparednessResult.ReadyToSign
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.Error.ErrorWhileSigning
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult.TransactionSigned
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionWithLedgerPayload
import com.algorand.android.module.transaction.component.domain.sign.model.TransactionSigner
import com.algorand.android.module.transaction.component.domain.sign.model.TransactionSigner.Algo25
import com.algorand.android.module.transaction.component.domain.sign.model.TransactionSigner.LedgerBle
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SignTransactionManagerImpl @Inject constructor(
    private val getSignTransactionPreparedness: GetSignTransactionPreparedness,
    private val getTransactionSigner: GetTransactionSigner,
    private val notReadyToSignToResultMapper: SignTransactionNotReadyToSignToSignTransactionResultMapper,
    private val signTransactionWithLedgerManager: SignTransactionWithLedgerManager,
    private val signTransactionWithSecretKey: SignTransactionWithSecretKey
) : SignTransactionManager {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var signJob: Job? = null

    private val _signTransactionResultFlow = MutableStateFlow<Event<SignTransactionResult>?>(null)
    override val signTransactionResultFlow: Flow<Event<SignTransactionResult>?>
        get() = _signTransactionResultFlow.asStateFlow()

    override fun setup(lifecycle: Lifecycle) {
        signTransactionWithLedgerManager.setup(lifecycle)
    }

    override fun sign(address: String, transaction: ByteArray) {
        if (signJob?.isActive == true) {
            signJob?.cancel()
        }
        coroutineScope.launch {
            val signer = getTransactionSigner(address)
            when (val preparednessResult = getSignTransactionPreparedness(signer)) {
                ReadyToSign -> signTransaction(signer, transaction)
                is NotReadyToSign -> showSignTransactionNotReadyResult(preparednessResult)
            }
        }
    }

    override fun stopAllResources() {
        signTransactionWithLedgerManager.stopAllProcess()
    }

    private fun signTransaction(signer: TransactionSigner, transaction: ByteArray) {
        when (signer) {
            is Algo25 -> signWithSK(signer, transaction)
            is LedgerBle -> signWithLedger(signer, transaction)
            else -> Unit // TODO Log here. We should never reach this point.
        }
    }

    private fun showSignTransactionNotReadyResult(result: NotReadyToSign) {
        _signTransactionResultFlow.update { Event(notReadyToSignToResultMapper(result)) }
    }

    private fun signWithLedger(signer: LedgerBle, transaction: ByteArray) {
        signTransactionWithLedgerManager.apply {
            collectorSignTransactionWithLedgerResult()
            sign(getLedgerPayload(signer, transaction))
        }
    }

    private fun collectorSignTransactionWithLedgerResult() {
        coroutineScope.launch {
            signTransactionWithLedgerManager.signTransactionWithLedgerResultFlow.collectLatest { result ->
                if (result != null) {
                    _signTransactionResultFlow.update { Event(result) }
                }
            }
        }
    }

    private fun signWithSK(signer: Algo25, transaction: ByteArray) {
        val signedTransaction = signTransactionWithSecretKey(signer.secretKey, transaction)
        _signTransactionResultFlow.update {
            val result = if (signedTransaction != null) TransactionSigned(signedTransaction) else ErrorWhileSigning
            Event(result)
        }
    }

    private fun getLedgerPayload(signer: LedgerBle, transaction: ByteArray): SignTransactionWithLedgerPayload {
        return SignTransactionWithLedgerPayload(
            accountAddress = signer.address,
            ledgerBleAddress = signer.bluetoothAddress,
            transaction = transaction
        )
    }
}

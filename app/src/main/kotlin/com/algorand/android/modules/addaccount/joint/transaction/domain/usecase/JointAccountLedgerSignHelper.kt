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

package com.algorand.android.modules.addaccount.joint.transaction.domain.usecase

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.util.Encoder
import com.algorand.android.R
import com.algorand.android.ledger.CustomScanCallback
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.ledger.operations.ExternalTransaction
import com.algorand.android.ledger.operations.ExternalTransactionOperation
import com.algorand.android.models.LedgerBleResult
import com.algorand.android.utils.Event
import com.algorand.android.utils.LifecycleScopedCoroutineOwner
import com.algorand.wallet.jointaccount.transaction.domain.model.AddSignatureInput
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.usecase.AddJointAccountSignature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class JointAccountLedgerSignHelper @Inject constructor(
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val addJointAccountSignature: AddJointAccountSignature
) : LifecycleScopedCoroutineOwner() {

    private val _signResultFlow = MutableStateFlow<LedgerSignResult>(LedgerSignResult.Idle)
    val signResultFlow: StateFlow<LedgerSignResult> = _signResultFlow

    private var currentSignRequest: SignRequest? = null
    private var currentTransactionIndex = 0
    private var signedTransactions = mutableListOf<ByteArray>()

    private val scanCallback = object : CustomScanCallback() {
        override fun onLedgerScanned(
            device: BluetoothDevice,
            currentTransactionIndex: Int?,
            totalTransactionCount: Int?
        ) {
            ledgerBleSearchManager.stop()
            currentScope.launch {
                currentSignRequest?.let { request ->
                    val rawTxBytes = request.rawTransactions.getOrNull(
                        this@JointAccountLedgerSignHelper.currentTransactionIndex
                    )
                    if (rawTxBytes != null) {
                        val transaction = JointAccountExternalTransaction(
                            transactionByteArray = rawTxBytes,
                            accountAddress = request.accountAddress,
                        )
                        ledgerBleOperationManager.startLedgerOperation(
                            ExternalTransactionOperation(device, transaction),
                            this@JointAccountLedgerSignHelper.currentTransactionIndex,
                            request.rawTransactions.size
                        )
                    }
                }
            }
        }

        override fun onScanError(errorMessageResId: Int, titleResId: Int) {
            _signResultFlow.value = LedgerSignResult.Error(errorMessageResId)
        }
    }

    private val operationManagerCollectorAction: (suspend (Event<LedgerBleResult>?) -> Unit) = { event ->
        event?.consume()?.let { ledgerBleResult ->
            when (ledgerBleResult) {
                is LedgerBleResult.SignedTransactionResult -> {
                    handleSignedTransaction(ledgerBleResult.transactionByteArray)
                }

                is LedgerBleResult.LedgerWaitingForApproval -> {
                    _signResultFlow.value = LedgerSignResult.WaitingForApproval(
                        ledgerBleResult.bluetoothName,
                        ledgerBleResult.currentTransactionIndex,
                        ledgerBleResult.totalTransactionCount
                    )
                }

                is LedgerBleResult.AppErrorResult -> {
                    _signResultFlow.value = LedgerSignResult.Error(ledgerBleResult.errorMessageId)
                }

                is LedgerBleResult.LedgerErrorResult -> {
                    _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
                }

                is LedgerBleResult.OperationCancelledResult -> {
                    _signResultFlow.value = LedgerSignResult.Cancelled
                }

                else -> Unit
            }
        }
    }

    fun setup(lifecycle: Lifecycle) {
        assignToLifecycle(lifecycle)
        ledgerBleOperationManager.setup(lifecycle)
        currentScope.launch {
            ledgerBleOperationManager.ledgerBleResultFlow.collect(operationManagerCollectorAction)
        }
    }

    fun signWithLedger(
        signRequestId: String,
        accountAddress: String,
        rawTransactionsBase64: List<String>,
        ledgerBluetoothAddress: String,
        ledgerAccountIndex: Int
    ) {
        resetSigningState()

        val rawTransactions = decodeBase64Transactions(rawTransactionsBase64)
        if (rawTransactions.isEmpty()) {
            _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
            return
        }

        currentSignRequest = createSignRequest(
            signRequestId, accountAddress, rawTransactions, ledgerBluetoothAddress, ledgerAccountIndex
        )
        _signResultFlow.value = LedgerSignResult.Scanning

        startLedgerConnection(ledgerBluetoothAddress, rawTransactions.size)
    }

    private fun resetSigningState() {
        currentTransactionIndex = 0
        signedTransactions.clear()
        _signResultFlow.value = LedgerSignResult.Idle
    }

    private fun decodeBase64Transactions(rawTransactionsBase64: List<String>): List<ByteArray> {
        return rawTransactionsBase64.mapNotNull { base64 ->
            runCatching { android.util.Base64.decode(base64, android.util.Base64.DEFAULT) }.getOrNull()
        }
    }

    private fun createSignRequest(
        signRequestId: String,
        accountAddress: String,
        rawTransactions: List<ByteArray>,
        ledgerBluetoothAddress: String,
        ledgerAccountIndex: Int
    ): SignRequest {
        return SignRequest(
            signRequestId = signRequestId,
            accountAddress = accountAddress,
            rawTransactions = rawTransactions,
            ledgerBluetoothAddress = ledgerBluetoothAddress,
            ledgerAccountIndex = ledgerAccountIndex
        )
    }

    private fun startLedgerConnection(ledgerBluetoothAddress: String, transactionCount: Int) {
        val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
        val isAlreadyConnected = currentConnectedDevice != null &&
            currentConnectedDevice.address == ledgerBluetoothAddress

        if (isAlreadyConnected) {
            scanCallback.onLedgerScanned(currentConnectedDevice!!, 0, transactionCount)
        } else {
            ledgerBleSearchManager.scan(
                newScanCallback = scanCallback,
                filteredAddress = ledgerBluetoothAddress,
                coroutineScope = currentScope
            )
        }
    }

    private fun handleSignedTransaction(signedTransactionData: ByteArray) {
        signedTransactions.add(signedTransactionData)
        currentTransactionIndex++

        currentSignRequest?.let { request ->
            if (currentTransactionIndex < request.rawTransactions.size) {
                val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
                if (currentConnectedDevice != null) {
                    scanCallback.onLedgerScanned(
                        currentConnectedDevice,
                        currentTransactionIndex,
                        request.rawTransactions.size
                    )
                } else {
                    _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
                }
            } else {
                submitSignatures(request)
            }
        }
    }

    private fun submitSignatures(request: SignRequest) {
        currentScope.launch {
            _signResultFlow.value = LedgerSignResult.Submitting

            val signatures = signedTransactions.mapNotNull { signedTx ->
                extractSignatureFromSignedTransaction(signedTx)
            }

            if (signatures.size != request.rawTransactions.size) {
                _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
                return@launch
            }

            val addSignatureInput = AddSignatureInput(
                address = request.accountAddress,
                response = SignRequestResponseType.SIGNED,
                signatures = listOf(signatures.map { Encoder.encodeToBase64(it) })
            )

            addJointAccountSignature(
                signRequestId = request.signRequestId,
                addSignatureInput = addSignatureInput
            ).use(
                onSuccess = {
                    _signResultFlow.value = LedgerSignResult.Success
                },
                onFailed = { _, _ ->
                    _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
                }
            )
        }
    }

    private fun extractSignatureFromSignedTransaction(signedTx: ByteArray): ByteArray? {
        return try {
            // The signed transaction contains the signature in the first 64 bytes after the 'sig' key
            // We use Encoder to decode and extract the signature
            val signedTransaction = Encoder.decodeFromMsgPack(
                signedTx,
                SignedTransaction::class.java
            )
            signedTransaction.sig?.bytes
        } catch (e: Exception) {
            null
        }
    }

    fun cancel() {
        ledgerBleSearchManager.stop()
        ledgerBleOperationManager.manualStopAllProcess()
        _signResultFlow.value = LedgerSignResult.Cancelled
    }

    /**
     * Reset the state to Idle. Should be called after handling Success or Error results.
     */
    fun resetState() {
        _signResultFlow.value = LedgerSignResult.Idle
    }

    override fun stopAllResources() {
        ledgerBleSearchManager.stop()
        ledgerBleOperationManager.manualStopAllProcess()
        currentSignRequest = null
        signedTransactions.clear()
    }

    private data class SignRequest(
        val signRequestId: String,
        val accountAddress: String,
        val rawTransactions: List<ByteArray>,
        val ledgerBluetoothAddress: String,
        val ledgerAccountIndex: Int
    )

    /**
     * Implementation of ExternalTransaction for joint account Ledger signing
     */
    private class JointAccountExternalTransaction(
        override val transactionByteArray: ByteArray,
        override val accountAddress: String,
    ) : ExternalTransaction {
        override val isRekeyedToAnotherAccount: Boolean = false
        override val accountAuthAddress: String? = null
    }

    sealed class LedgerSignResult {
        data object Idle : LedgerSignResult()
        data object Scanning : LedgerSignResult()
        data class WaitingForApproval(
            val bluetoothName: String?,
            val currentTransactionIndex: Int?,
            val totalTransactionCount: Int?
        ) : LedgerSignResult()

        data object Submitting : LedgerSignResult()
        data object Success : LedgerSignResult()
        data object Cancelled : LedgerSignResult()
        data class Error(val errorMessageResId: Int) : LedgerSignResult()
    }
}

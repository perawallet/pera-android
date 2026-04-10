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
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
import com.algorand.wallet.jointaccount.transaction.domain.usecase.AddJointAccountSignature
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class JointAccountLedgerSignHelper @Inject constructor(
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val addJointAccountSignature: AddJointAccountSignature,
    private val parseTransactionMessagePack: ParseTransactionMessagePack
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
                val request = currentSignRequest ?: return@launch
                advanceToNextJointTransaction(request)

                if (this@JointAccountLedgerSignHelper.currentTransactionIndex >= request.rawTransactions.size) {
                    submitSignatures(request)
                    return@launch
                }

                val rawTxBytes = request.rawTransactions[this@JointAccountLedgerSignHelper.currentTransactionIndex]
                ledgerBleOperationManager.startLedgerOperation(
                    ExternalTransactionOperation(device, request.toExternalTransaction(rawTxBytes)),
                    this@JointAccountLedgerSignHelper.currentTransactionIndex,
                    request.jointAccountTransactionIndices.size
                )
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
                    val request = currentSignRequest
                    if (request != null) {
                        _signResultFlow.value = LedgerSignResult.RejectedOnDevice(
                            signRequestId = request.signRequestId,
                            accountAddress = request.accountAddress
                        )
                    } else {
                        _signResultFlow.value = LedgerSignResult.Cancelled
                    }
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
        ledgerAccountIndex: Int,
        accountAuthAddress: String? = null,
        isRekeyedToAnotherAccount: Boolean = false,
        jointAccountAddress: String? = null
    ) {
        resetSigningState()

        val rawTransactions = decodeBase64Transactions(rawTransactionsBase64)
        if (rawTransactions.isEmpty()) {
            _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
            return
        }

        val jointTxIndices = if (jointAccountAddress != null) {
            getJointAccountTransactionIndices(rawTransactions, jointAccountAddress)
        } else {
            rawTransactions.indices.toSet()
        }

        currentSignRequest = SignRequest(
            signRequestId = signRequestId,
            accountAddress = accountAddress,
            rawTransactions = rawTransactions,
            ledgerBluetoothAddress = ledgerBluetoothAddress,
            ledgerAccountIndex = ledgerAccountIndex,
            accountAuthAddress = accountAuthAddress,
            isRekeyedToAnotherAccount = isRekeyedToAnotherAccount,
            jointAccountTransactionIndices = jointTxIndices
        )
        _signResultFlow.value = LedgerSignResult.Scanning

        val jointTxCount = jointTxIndices.size
        startLedgerConnection(ledgerBluetoothAddress, jointTxCount)
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

    private fun startLedgerConnection(ledgerBluetoothAddress: String, transactionCount: Int) {
        val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
        val isAlreadyConnected = currentConnectedDevice != null &&
            currentConnectedDevice.address == ledgerBluetoothAddress

        if (isAlreadyConnected) {
            scanCallback.onLedgerScanned(currentConnectedDevice!!, 0, transactionCount)
        } else {
            currentScope.launch {
                if (currentConnectedDevice != null) {
                    ledgerBleOperationManager.disconnectCurrentDevice()
                    delay(DISCONNECT_BEFORE_RECONNECT_DELAY_MS)
                }
                ledgerBleSearchManager.scan(
                    coroutineScope = currentScope,
                    newScanCallback = scanCallback,
                    filteredAddress = ledgerBluetoothAddress
                )
            }
        }
    }

    private companion object {
        private const val DISCONNECT_BEFORE_RECONNECT_DELAY_MS = 500L
    }

    private fun handleSignedTransaction(signedTransactionData: ByteArray) {
        signedTransactions.add(signedTransactionData)
        currentTransactionIndex++

        val request = currentSignRequest ?: return
        if (currentTransactionIndex >= request.rawTransactions.size) {
            submitSignatures(request)
            return
        }
        val device = ledgerBleOperationManager.connectedBluetoothDevice
        if (device != null) {
            scanCallback.onLedgerScanned(device, currentTransactionIndex, request.rawTransactions.size)
        } else {
            _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
        }
    }

    private fun submitSignatures(request: SignRequest) {
        currentScope.launch {
            _signResultFlow.value = LedgerSignResult.Submitting

            val ledgerSignatures = signedTransactions.mapNotNull(::extractSignatureFromSignedTransaction)
            if (ledgerSignatures.size != request.jointAccountTransactionIndices.size) {
                _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred)
                return@launch
            }

            val allSignatures = buildSignatureList(request, ledgerSignatures)
            val input = AddSignatureInput(
                address = request.accountAddress,
                response = SignRequestResponseType.SIGNED,
                signatures = listOf(allSignatures)
            )

            addJointAccountSignature(request.signRequestId, listOf(input)).use(
                onSuccess = { _signResultFlow.value = LedgerSignResult.Success },
                onFailed = { _, _ -> _signResultFlow.value = LedgerSignResult.Error(R.string.an_error_occurred) }
            )
        }
    }

    private fun buildSignatureList(request: SignRequest, ledgerSignatures: List<ByteArray>): List<String?> {
        var ledgerSigIndex = 0
        return request.rawTransactions.indices.map { index ->
            if (index in request.jointAccountTransactionIndices) {
                Encoder.encodeToBase64(ledgerSignatures[ledgerSigIndex++])
            } else {
                null
            }
        }
    }

    private fun advanceToNextJointTransaction(request: SignRequest) {
        while (currentTransactionIndex < request.rawTransactions.size &&
            currentTransactionIndex !in request.jointAccountTransactionIndices
        ) {
            currentTransactionIndex++
        }
    }

    private fun extractSignatureFromSignedTransaction(signedTx: ByteArray): ByteArray? {
        return runCatching {
            Encoder.decodeFromMsgPack(signedTx, SignedTransaction::class.java).sig?.bytes
        }.getOrNull()
    }

    fun cancel() {
        ledgerBleSearchManager.stop()
        ledgerBleOperationManager.manualStopAllProcess()
        _signResultFlow.value = LedgerSignResult.Cancelled
    }

    fun resetState() {
        _signResultFlow.value = LedgerSignResult.Idle
    }

    override fun stopAllResources() {
        ledgerBleSearchManager.stop()
        ledgerBleOperationManager.manualStopAllProcess()
        currentSignRequest = null
        signedTransactions.clear()
    }

    private fun getJointAccountTransactionIndices(
        rawTransactions: List<ByteArray>,
        jointAccountAddress: String
    ): Set<Int> {
        return rawTransactions.indices.filter { index ->
            val senderAddress = parseTransactionMessagePack(rawTransactions[index])
                ?.senderAddress?.decodedAddress
            senderAddress == jointAccountAddress
        }.toSet()
    }

    private data class SignRequest(
        val signRequestId: String,
        val accountAddress: String,
        val rawTransactions: List<ByteArray>,
        val ledgerBluetoothAddress: String,
        val ledgerAccountIndex: Int,
        val accountAuthAddress: String? = null,
        val isRekeyedToAnotherAccount: Boolean = false,
        val jointAccountTransactionIndices: Set<Int> = emptySet()
    ) {
        fun toExternalTransaction(txBytes: ByteArray) = object : ExternalTransaction {
            override val transactionByteArray = txBytes
            override val accountAddress = this@SignRequest.accountAddress
            override val accountAuthAddress = this@SignRequest.accountAuthAddress
            override val isRekeyedToAnotherAccount = this@SignRequest.isRekeyedToAnotherAccount
        }
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
        data class RejectedOnDevice(
            val signRequestId: String,
            val accountAddress: String
        ) : LedgerSignResult()

        data class Error(val errorMessageResId: Int) : LedgerSignResult()
    }
}

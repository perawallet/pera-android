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

package com.algorand.android.module.ledger.manager.implementation

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import com.algorand.android.algosdk.component.AlgoSdkAddress
import com.algorand.android.algosdk.component.transaction.AlgoTransactionSigner
import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.coroutine.LifecycleScopedCoroutineOwner
import com.algorand.android.foundation.permission.PeraPermissionManager
import com.algorand.android.module.ledger.domain.LedgerBleObserver
import com.algorand.android.module.ledger.domain.model.LedgerBleResult
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.LedgerError
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.ReconnectLedger
import com.algorand.android.module.ledger.domain.model.LedgerBleResult.ErrorResult.TransmissionError
import com.algorand.android.module.ledger.domain.model.LedgerOperation
import com.algorand.android.module.ledger.domain.model.LedgerOperation.AccountFetchAllOperation
import com.algorand.android.module.ledger.domain.model.LedgerOperation.TransactionOperation
import com.algorand.android.module.ledger.domain.model.LedgerOperation.VerifyAddressOperation
import com.algorand.android.module.ledger.domain.payload.CreateSendPublicKeyRequestPayload
import com.algorand.android.module.ledger.domain.payload.CreateSignTransactionRequestPayload
import com.algorand.android.module.ledger.domain.payload.CreateVerifyPublicKeyRequestPayload
import com.algorand.android.module.ledger.manager.LedgerBleManager
import com.algorand.android.module.ledger.manager.LedgerBleOperationManager
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.observer.ConnectionObserver.REASON_NOT_SUPPORTED

internal class LedgerBleOperationManagerImpl @Inject constructor(
    private val cacheAccountDetail: CacheAccountDetail,
    private val ledgerBleManager: LedgerBleManager,
    private val algoSdkAddress: AlgoSdkAddress,
    private val algoTransactionSigner: AlgoTransactionSigner,
    private val createSignTransactionRequestPayload: CreateSignTransactionRequestPayload,
    private val createVerifyPublicKeyRequestPayload: CreateVerifyPublicKeyRequestPayload,
    private val createSendPublicKeyRequestPayload: CreateSendPublicKeyRequestPayload,
    private val peraPermissionManager: PeraPermissionManager
) : LifecycleScopedCoroutineOwner(), LedgerBleObserver, LedgerBleOperationManager {

    override val connectedBluetoothDevice: BluetoothDevice?
        get() = ledgerBleManager.connectedDevice

    private val _ledgerBleResultFlow = MutableStateFlow<Event<LedgerBleResult>?>(null)
    override val ledgerBleResultFlow: StateFlow<Event<LedgerBleResult>?>
        get() = _ledgerBleResultFlow.asStateFlow()

    private var currentOperation: LedgerOperation? = null

    override fun setup(lifecycle: Lifecycle) {
        ledgerBleManager.setObserver(this)
        assignToLifecycle(lifecycle)
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        postResult(LedgerBleResult.OnBondingFailed)
    }

    override fun startVerifyAddressOperation(operation: VerifyAddressOperation) {
        currentOperation = operation
        verifyPublicKeyRequest(operation)
    }

    override fun startTransactionOperation(operation: TransactionOperation) {
        currentOperation = operation
        sendPublicKeyRequest()
    }

    override fun startFetchAllAccountsOperation(operation: AccountFetchAllOperation) {
        currentOperation = operation
        sendPublicKeyRequest()
    }

    private fun verifyPublicKeyRequest(newOperation: VerifyAddressOperation) {
        currentScope.launch {
            currentOperation?.run {
                if (connectToLedger(bluetoothDevice)) {
                    val payload = createVerifyPublicKeyRequestPayload(newOperation.indexOfAddress)
                    ledgerBleManager.sendVerifyPublicKeyRequest(payload)
                    nextIndex++
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun sendTransactionRequest() {
        if (!peraPermissionManager.areBluetoothPermissionsGranted()) return
        (currentOperation as? TransactionOperation)?.run {
            val currentTransactionData = transactionByteArray
            if (currentTransactionData != null) {
                if (connectToLedger(bluetoothDevice)) {
                    val requestPayload = createSignTransactionRequestPayload(currentTransactionData, nextIndex - 1)
                    ledgerBleManager.sendSignTransactionRequest(requestPayload)
                    postResult(LedgerBleResult.LedgerWaitingForApproval(bluetoothDevice.name))
                }
            }
        }
    }

    private fun sendPublicKeyRequest() {
        currentOperation?.run {
            currentScope.launch {
                if (connectToLedger(bluetoothDevice)) {
                    val payload = createSendPublicKeyRequestPayload(nextIndex)
                    ledgerBleManager.sendPublicKeyRequest(payload)
                    nextIndex++
                }
            }
        }
    }

    override fun onDataReceived(device: BluetoothDevice, byteArray: ByteArray) {
        currentOperation?.run {
            when {
                this is TransactionOperation && isAddressVerified -> {
                    onTransactionSignatureReceived(byteArray)
                }
                else -> {
                    val accountPublicKey = algoSdkAddress.generateAddressFromPublicKey(byteArray)
                    if (accountPublicKey?.decodedAddress != null) {
                        currentScope.launch {
                            onPublicKeyReceived(device, accountPublicKey.decodedAddress.orEmpty())
                        }
                    }
                }
            }
        } ?: run {
            // TODO recordException(Exception("LedgerBleOperationManager::onDataReceived::currentOperation is null "))
        }
    }

    private suspend fun onPublicKeyReceived(device: BluetoothDevice, publicKey: String) {
        currentScope.launch(Dispatchers.IO) {
            currentOperation?.run {
                if (this is VerifyAddressOperation) {
                    val result = LedgerBleResult.VerifyPublicKeyResult(publicKey == address, publicKey, address)
                    postResult(result)
                    return@launch
                }

                if (this is TransactionOperation && isRekeyedToAnotherAccount && publicKey == accountAuthAddress) {
                    isAddressVerified = true
                    sendTransactionRequest()
                    return@launch
                }
                if (this is TransactionOperation && !isRekeyedToAnotherAccount && publicKey == accountAddress) {
                    isAddressVerified = true
                    sendTransactionRequest()
                    return@launch
                } else {
                    handleAccountInformation(this, publicKey, device)
                }
            }
        }
    }

    private suspend fun handleAccountInformation(
        ledgerOperation: LedgerOperation,
        publicKey: String,
        device: BluetoothDevice
    ) {
        cacheAccountDetail(publicKey).use(
            onSuccess = { accountInformation ->
                if (accountInformation.isCreated() || ledgerOperation.nextIndex == 1) {
                    if (ledgerOperation is AccountFetchAllOperation) {
                        ledgerOperation.accounts.add(accountInformation)
                    }
                    sendPublicKeyRequest()
                } else {
                    // all the accounts are fetched.
                    val result = when (ledgerOperation) {
                        is AccountFetchAllOperation -> LedgerBleResult.AccountResult(ledgerOperation.accounts, device)
                        is TransactionOperation -> ReconnectLedger
                        is VerifyAddressOperation -> throw Exception("Verify should not posted here.")
                    }
                    postResult(result)
                }
            },
            onFailed = { _, _ ->
                postResult(LedgerBleResult.ErrorResult.NetworkError)
            }
        )
    }

    private fun onTransactionSignatureReceived(transactionSignature: ByteArray) {
        try {
            (currentOperation as? TransactionOperation)?.run {
                val signedTransactionData = with(algoTransactionSigner) {
                    if (isRekeyedToAnotherAccount) {
                        attachSignatureWithSigner(transactionSignature, transactionByteArray, accountAuthAddress)
                    } else {
                        attachSignature(transactionSignature, transactionByteArray)
                    }
                }
                postResult(LedgerBleResult.SignedTransactionResult(signedTransactionData!!))
            }
        } catch (exception: Exception) {
            // TODO recordException(exception)
            postResult(LedgerError(exception.message.toString()))
        }
    }

    override fun onMissingBytes(device: BluetoothDevice) {
        postResult(LedgerBleResult.OnMissingBytes(device))
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        when (reason) {
            REASON_NOT_SUPPORTED -> postResult(LedgerBleResult.ErrorResult.UnsupportedDeviceError)
            else -> {
                // TODO sendErrorLog("Unhandled else case in LedgerBleOperationManager.onDeviceFailedToConnect")
            }
        }
    }

    override fun onOperationCancelled() {
        postResult(LedgerBleResult.OperationCancelledResult)
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        postResult(LedgerBleResult.OnLedgerDisconnected)
    }

    override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
        val error = if (errorCode == ERROR_ON_WRITE_CHARACTERISTIC) TransmissionError else LedgerError(message)
        postResult(error)
    }

    private fun postResult(ledgerBleResult: LedgerBleResult) {
        _ledgerBleResultFlow.value = Event(ledgerBleResult)
    }

    private suspend fun connectToLedger(bluetoothDevice: BluetoothDevice): Boolean {
        if (ledgerBleManager.connectedDevice?.address == bluetoothDevice.address) {
            // ledger is already connect to connectionManager.
            return true
        }
        ledgerBleManager.connectToDevice(bluetoothDevice)
        while (ledgerBleManager.isManagerReady().not()) {
            delay(LEDGER_CONNECTION_DELAY)
            if (ledgerBleManager.isTryingToConnect().not()) {
                postResult(LedgerBleResult.ErrorResult.ConnectionError)
                return false
            }
        }
        return true
    }

    override fun stopAllProcess() {
        currentScope.coroutineContext.cancelChildren()
        stopAllResources()
    }

    override fun stopAllResources() {
        _ledgerBleResultFlow.value = null
        currentOperation = null
        ledgerBleManager.disconnectFromDevice()
    }

    companion object {
        private const val ERROR_ON_WRITE_CHARACTERISTIC = 133
        private const val LEDGER_CONNECTION_DELAY = 250L
    }
}

// TODO CHECK Android 14 bonding error
// TODO After first bonding, we get error, when we restart, issue goes away
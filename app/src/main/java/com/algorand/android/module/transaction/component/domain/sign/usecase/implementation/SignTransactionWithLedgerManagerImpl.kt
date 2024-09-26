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

package com.algorand.android.module.transaction.component.domain.sign.usecase.implementation

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.foundation.Event
import com.algorand.android.module.ledger.domain.helper.LedgerBleScanCallback
import com.algorand.android.module.ledger.domain.model.LedgerBleResult
import com.algorand.android.module.ledger.domain.model.LedgerOperation
import com.algorand.android.module.ledger.manager.LedgerBleOperationManager
import com.algorand.android.module.ledger.manager.LedgerBleSearchManager
import com.algorand.android.module.transaction.component.domain.sign.mapper.LedgerBleResultSignTransactionResultMapper
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionResult
import com.algorand.android.module.transaction.component.domain.sign.model.SignTransactionWithLedgerPayload
import com.algorand.android.module.transaction.component.domain.sign.usecase.SignTransactionWithLedgerManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SignTransactionWithLedgerManagerImpl @Inject constructor(
    private val ledgerBleOperationManager: LedgerBleOperationManager,
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val getAccountInformation: GetAccountInformation,
    private val ledgerBleResultSignTransactionResultMapper: LedgerBleResultSignTransactionResultMapper
) : SignTransactionWithLedgerManager {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var sendTransactionJob: Job? = null

    private val _signTransactionWithLedgerResultFlow = MutableStateFlow<SignTransactionResult?>(null)
    override val signTransactionWithLedgerResultFlow
        get() = _signTransactionWithLedgerResultFlow.asStateFlow()

    private var payload: SignTransactionWithLedgerPayload? = null

    private val ledgerBleScanCallback = object : LedgerBleScanCallback() {
        override fun onLedgerScanned(device: BluetoothDevice) {
            ledgerBleSearchManager.stop()
            sendCurrentTransaction(device)
        }

        override fun onScanError() {
            _signTransactionWithLedgerResultFlow.update { SignTransactionResult.Error.ScannedLedgerNotFoundError }
        }
    }

    private val ledgerBleResultCollector: suspend (Event<LedgerBleResult>?) -> Unit = { result ->
        result?.consume()?.let { ledgerBleResult ->
            Log.e("LedgerBleResult", ledgerBleResult.toString())
            _signTransactionWithLedgerResultFlow.update {
                ledgerBleResultSignTransactionResultMapper(ledgerBleResult)
            }
        }
    }

    override fun setup(lifecycle: Lifecycle) {
        ledgerBleOperationManager.setup(lifecycle)
        coroutineScope.launch {
            ledgerBleOperationManager.ledgerBleResultFlow.collect(ledgerBleResultCollector)
        }
    }

    override fun sign(payload: SignTransactionWithLedgerPayload) {
        this.payload = payload
        val currentConnectedDevice = ledgerBleOperationManager.connectedBluetoothDevice
        if (currentConnectedDevice != null && currentConnectedDevice.address == payload.ledgerBleAddress) {
            sendCurrentTransaction(currentConnectedDevice)
        } else {
            searchForDevice(payload.ledgerBleAddress)
        }
    }

    override fun stopAllProcess() {
        ledgerBleSearchManager.stop()
        ledgerBleOperationManager.stopAllProcess()
        sendTransactionJob?.cancel()
        coroutineScope.coroutineContext.cancelChildren()
        payload = null
    }

    private fun sendCurrentTransaction(currentConnectedDevice: BluetoothDevice) {
        if (sendTransactionJob?.isActive == true) return
        sendTransactionJob = coroutineScope.launch {
            val operation = getTransactionOperation(currentConnectedDevice) ?: return@launch
            ledgerBleOperationManager.startTransactionOperation(operation)
        }
    }

    private fun searchForDevice(bluetoothAddress: String) {
        coroutineScope.launch {
            ledgerBleSearchManager.scan(ledgerBleScanCallback, filteredAddress = bluetoothAddress)
        }
    }

    private suspend fun getTransactionOperation(device: BluetoothDevice): LedgerOperation.TransactionOperation? {
        return payload?.run {
            val accountInfo = getAccountInformation(accountAddress) ?: return null
            val isRekeyed = accountInfo.isRekeyed()
            val authAddress = accountInfo.rekeyAdminAddress
            LedgerOperation.TransactionOperation(device, transaction, accountAddress, isRekeyed, authAddress)
        }
    }
}

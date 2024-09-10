/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.baseledgersearch.ledgersearch.ui

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.foundation.Event
import com.algorand.android.ledger.domain.helper.IsBluetoothBondingRequired
import com.algorand.android.ledger.domain.helper.LedgerBleScanCallback
import com.algorand.android.ledger.domain.model.LedgerBleResult
import com.algorand.android.ledger.domain.model.LedgerOperation
import com.algorand.android.ledger.manager.LedgerBleOperationManager
import com.algorand.android.ledger.manager.LedgerBleSearchManager
import com.algorand.android.ledger.ui.LedgerFetchAllAccountsOperationViewModel
import com.algorand.android.modules.baseledgersearch.ledgersearch.ui.model.LedgerBaseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LedgerSearchViewModel @Inject constructor(
    private val ledgerBleSearchManager: LedgerBleSearchManager,
    private val isBluetoothBondingRequired: IsBluetoothBondingRequired,
    private val ledgerBleOperationManager: LedgerBleOperationManager
) : BaseViewModel(), LedgerFetchAllAccountsOperationViewModel {

    val ledgerDevicesLiveData = MutableLiveData<List<LedgerBaseItem>>()

    private val addressLedgerDeviceMap = HashMap<String, BluetoothDevice>()

    private val scanCallback = object : LedgerBleScanCallback() {
        override fun onLedgerScanned(device: BluetoothDevice) {
            if (!addressLedgerDeviceMap.containsKey(device.address)) {
                addressLedgerDeviceMap[device.address] = device
                val ledgerDeviceSet = addressLedgerDeviceMap.values.toSet()
                mutableListOf<LedgerBaseItem>().apply {
                    add(LedgerBaseItem.LedgerLoadingItem)
                    addAll(ledgerDeviceSet.map { LedgerBaseItem.LedgerItem(it) })
                    ledgerDevicesLiveData.postValue(this)
                }
            }
        }

        override fun onScanError() {
            // Nothing to do here.
        }
    }

    init {
        ledgerDevicesLiveData.postValue(listOf(LedgerBaseItem.LedgerLoadingItem))
    }

    fun isBleBondingRequired(bleAddress: String): Boolean {
        return isBluetoothBondingRequired(bleAddress)
    }

    fun startBluetoothSearch() {
        viewModelScope.launch {
            ledgerBleSearchManager.scan(scanCallback)
        }
    }

    fun stopBluetoothSearch() {
        ledgerBleSearchManager.stop()
    }

    override fun startFetchAllAccountsOperation(operation: LedgerOperation.AccountFetchAllOperation) {
        ledgerBleOperationManager.startFetchAllAccountsOperation(operation)
    }

    override fun stopLedgerOperation() {
        ledgerBleOperationManager.stopAllProcess()
    }

    override fun getLedgerBleOperationResultFlow(): StateFlow<Event<LedgerBleResult>?> {
        return ledgerBleOperationManager.ledgerBleResultFlow
    }

    override fun setupLedgerOperationManager(lifecycle: Lifecycle) {
        ledgerBleOperationManager.setup(lifecycle)
    }
}

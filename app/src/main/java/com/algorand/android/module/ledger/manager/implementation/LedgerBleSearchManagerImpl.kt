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

import com.algorand.android.module.foundation.permission.PeraPermissionManager
import com.algorand.android.module.ledger.domain.helper.LedgerBleScanCallback
import com.algorand.android.module.ledger.domain.helper.LedgerBleScanner
import com.algorand.android.module.ledger.domain.helper.LedgerBleSearchTimer
import com.algorand.android.module.ledger.manager.LedgerBleManager
import com.algorand.android.module.ledger.manager.LedgerBleSearchManager
import javax.inject.Inject

internal class LedgerBleSearchManagerImpl @Inject constructor(
    private val ledgerBleManager: LedgerBleManager,
    private val ledgerBleSearchTimer: LedgerBleSearchTimer,
    private val ledgerBleScanner: LedgerBleScanner,
    private val peraPermissionManager: PeraPermissionManager
) : LedgerBleSearchManager {

    private var isScanning = false
    private var scanCallback: LedgerBleScanCallback? = null

    override suspend fun scan(newScanCallback: LedgerBleScanCallback, filteredAddress: String?) {
        if (filteredAddress != null && isLedgerConnected(filteredAddress) || isScanning) {
            // Don't need to scan any ledger devices because it has already connected.
            return
        }

        isScanning = true
        setScanCallback(newScanCallback, filteredAddress)
        startScanning(filteredAddress)
    }

    private suspend fun startScanning(filteredAddress: String?) {
        if (peraPermissionManager.areBluetoothPermissionsGranted()) {
            scanCallback?.run { ledgerBleScanner.startScan(this) }
            if (filteredAddress != null) {
                startSearchTimer()
            } else {
                provideConnectedLedger()
            }
        } else {
            return
        }
    }

    private fun setScanCallback(callback: LedgerBleScanCallback, filteredAddress: String?) {
        this.scanCallback = callback.apply {
            setFilterAddress(filteredAddress)
        }
    }

    private suspend fun startSearchTimer() {
        ledgerBleSearchTimer.startTimer {
            scanCallback?.onScanError()
            stop()
        }
    }

    private fun provideConnectedLedger() {
        val connectedDevice = ledgerBleManager.connectedDevice
        if (connectedDevice != null) {
            scanCallback?.onLedgerScanned(connectedDevice)
        }
    }

    private fun isLedgerConnected(deviceAddress: String): Boolean {
        val connectedDevice = ledgerBleManager.connectedDevice
        if (connectedDevice != null && connectedDevice.address == deviceAddress) {
            scanCallback?.onLedgerScanned(connectedDevice)
            return true
        }
        return false
    }

    override fun stop() {
        ledgerBleSearchTimer.stopTimer()
        scanCallback?.run {
            if (peraPermissionManager.areBluetoothPermissionsGranted()) {
                ledgerBleScanner.stopScan(this)
            } else {
                // TODO sendErrorLog("Bluetooth permission revoked before scanning could stop.")
                return
            }
        }
        isScanning = false
    }
}

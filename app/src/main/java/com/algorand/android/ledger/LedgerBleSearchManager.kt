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

package com.algorand.android.ledger

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import com.algorand.android.R
import com.algorand.android.ledger.LedgerBleConnectionManager.Companion.FLEX_SERVICE_UUID
import com.algorand.android.ledger.LedgerBleConnectionManager.Companion.NANOX_SERVICE_UUID
import com.algorand.android.ledger.LedgerBleConnectionManager.Companion.STAX_SERVICE_UUID
import com.algorand.android.utils.areBluetoothPermissionsGranted
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.sendErrorLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class LedgerBleSearchManager(
    private val context: Context,
    private val bluetoothManager: BluetoothManager?,
    private val ledgerBleConnectionManager: LedgerBleConnectionManager
) {

    private var isScanning = false
    private var scanCallback: CustomScanCallback? = null
    private var timeoutJob: Job? = null

    @SuppressLint("MissingPermission")
    fun scan(
        coroutineScope: CoroutineScope,
        newScanCallback: CustomScanCallback,
        currentTransactionIndex: Int? = null,
        totalTransactionCount: Int? = null,
        filteredAddress: String? = null
    ) {
        if (filteredAddress != null &&
            isLedgerConnected(
                deviceAddress = filteredAddress,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount
            )
        ) {
            // Don't need to scan any ledger devices because it has already connected.
            return
        }

        if (isScanning) return
        isScanning = true
        this.scanCallback = newScanCallback.apply {
            this.filteredAddress = filteredAddress
            this.currentTransactionIndex = currentTransactionIndex
            this.totalTransactionCount = totalTransactionCount
        }

        val serviceUuids = listOf(NANOX_SERVICE_UUID, FLEX_SERVICE_UUID, STAX_SERVICE_UUID)

        val scanFilters = serviceUuids.map { uuid ->
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(uuid))
                .build()
        }

        val scanSettings = ScanSettings.Builder().build()

        if (context.areBluetoothPermissionsGranted()) {
            bluetoothManager?.adapter?.bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
            if (filteredAddress != null) {
                startTimeout(coroutineScope)
            } else {
                provideConnectedLedger(
                    currentTransactionIndex = currentTransactionIndex,
                    totalTransactionCount = totalTransactionCount
                )
            }
        } else {
            return
        }
    }

    private fun startTimeout(coroutineScope: CoroutineScope) {
        timeoutJob?.cancel()
        timeoutJob = coroutineScope.launchIO {
            delay(MAX_SCAN_DURATION)
            scanCallback?.onScanError(R.string.error_connection_message, R.string.error_connection_title)
            stop()
        }
    }

    private fun provideConnectedLedger(currentTransactionIndex: Int?, totalTransactionCount: Int?) {
        val connectedDevice = ledgerBleConnectionManager.bluetoothDevice
        if (connectedDevice != null) {
            scanCallback?.onLedgerScanned(
                device = connectedDevice,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount
            )
        }
    }

    private fun isLedgerConnected(
        deviceAddress: String,
        currentTransactionIndex: Int?,
        totalTransactionCount: Int?
    ): Boolean {
        val connectedDevice = ledgerBleConnectionManager.bluetoothDevice
        if (connectedDevice != null && connectedDevice.address == deviceAddress) {
            scanCallback?.onLedgerScanned(
                device = connectedDevice,
                currentTransactionIndex = currentTransactionIndex,
                totalTransactionCount = totalTransactionCount
            )
            return true
        }
        return false
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        timeoutJob?.cancel()
        scanCallback?.run {
            if (context.areBluetoothPermissionsGranted()) {
                bluetoothManager?.adapter?.bluetoothLeScanner?.stopScan(this)
            } else {
                sendErrorLog("Bluetooth permission revoked before scanning could stop.")
                return
            }
        }
        isScanning = false
    }

    companion object {
        private const val MAX_SCAN_DURATION = 15000L
    }
}

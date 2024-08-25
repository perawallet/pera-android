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

package com.algorand.android.ledger.domain.helper

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import com.algorand.android.foundation.permission.PeraPermissionManager
import com.algorand.android.ledger.manager.implementation.LedgerBleManagerImpl
import javax.inject.Inject

internal class LedgerBleScannerImpl @Inject constructor(
    private val bluetoothLeScanner: BluetoothLeScanner?,
    private val peraPermissionManager: PeraPermissionManager
) : LedgerBleScanner {

    @SuppressLint("MissingPermission")
    override fun startScan(scanCallback: ScanCallback) {
        val scanFilters = getScanFilters()
        val scanSettings = getScanSettings()

        if (peraPermissionManager.areBluetoothPermissionsGranted()) {
            bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan(scanCallback: ScanCallback) {
        if (peraPermissionManager.areBluetoothPermissionsGranted()) {
            bluetoothLeScanner?.stopScan(scanCallback)
        }
    }

    private fun getScanFilters(): List<ScanFilter> {
        val ledgerScanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(LedgerBleManagerImpl.SERVICE_UUID))
            .build()
        return listOf(ledgerScanFilter)
    }

    private fun getScanSettings(): ScanSettings {
        return ScanSettings.Builder().build()
    }
}

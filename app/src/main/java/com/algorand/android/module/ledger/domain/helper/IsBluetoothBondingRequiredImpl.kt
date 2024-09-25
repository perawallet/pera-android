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

package com.algorand.android.module.ledger.domain.helper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import com.algorand.android.foundation.permission.PeraPermissionManager
import javax.inject.Inject

internal class IsBluetoothBondingRequiredImpl @Inject constructor(
    private val peraPermissionManager: PeraPermissionManager,
    private val bluetoothAdapter: BluetoothAdapter?
) : IsBluetoothBondingRequired {

    @SuppressLint("MissingPermission")
    override fun invoke(bluetoothAddress: String): Boolean {
        return if (peraPermissionManager.areBluetoothPermissionsGranted()) {
            bluetoothAdapter?.bondedDevices?.all { it.address != bluetoothAddress } ?: true
        } else {
            false
        }
    }
}

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

package com.algorand.android.utils

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.location.LocationManagerCompat

fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

fun showEnableBluetoothPopup(resultLauncher: ActivityResultLauncher<Intent>) {
    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply { resultLauncher.launch(this) }
}

fun Context.checkIfBluetoothPermissionAreTaken(
    bluetoothResultLauncher: ActivityResultLauncher<Array<String>>,
    locationResultLauncher: ActivityResultLauncher<String>
): Boolean {
    if (!areBluetoothPermissionsGranted()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBluetoothScanConnectPermission(bluetoothResultLauncher)
        } else {
            requestLocationPermission(locationResultLauncher)
        }
        return false
    }
    return true
}

@RequiresApi(Build.VERSION_CODES.S)
private fun requestBluetoothScanConnectPermission(resultLauncher: ActivityResultLauncher<Array<String>>) {
    resultLauncher.launch(
        arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        )
    )
}

private fun requestLocationPermission(resultLauncher: ActivityResultLauncher<String>) {
    resultLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
}

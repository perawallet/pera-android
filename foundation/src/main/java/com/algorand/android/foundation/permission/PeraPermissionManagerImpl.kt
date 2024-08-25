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

package com.algorand.android.foundation.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

internal class PeraPermissionManagerImpl(private val context: Context) : PeraPermissionManager {

    override fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun areBluetoothPermissionsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isPermissionGranted(BLUETOOTH_SCAN_PERMISSION) && isPermissionGranted(BLUETOOTH_CONNECT_PERMISSION)
        } else {
            isPermissionGranted(LOCATION_PERMISSION)
        }
    }

    private companion object {
        @RequiresApi(Build.VERSION_CODES.S)
        const val BLUETOOTH_SCAN_PERMISSION = Manifest.permission.BLUETOOTH_SCAN

        @RequiresApi(Build.VERSION_CODES.S)
        const val BLUETOOTH_CONNECT_PERMISSION = Manifest.permission.BLUETOOTH_CONNECT
        const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }
}

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

package com.algorand.android.ledger.manager.implementation

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTING
import android.content.Context
import android.util.Log
import com.algorand.android.ledger.domain.LedgerBleObserver
import com.algorand.android.ledger.domain.helper.PacketizeLedgerPayload
import com.algorand.android.ledger.manager.LedgerBleManager
import com.algorand.android.ledger.domain.helper.LedgerDataReceivedCallback
import java.util.UUID
import no.nordicsemi.android.ble.BleManager

// Use with dagger.
// https://github.com/NordicSemiconductor/Android-BLE-Library/blob/master/MIGRATION.md
internal class LedgerBleManagerImpl(
    appContext: Context,
    private val packetizeLedgerPayload: PacketizeLedgerPayload,
    private val isDebug: Boolean
) : BleManager(appContext), LedgerBleManager {

    private var characteristicWrite: BluetoothGattCharacteristic? = null
    private var characteristicNotify: BluetoothGattCharacteristic? = null

    override val connectedDevice: BluetoothDevice?
        get() = bluetoothDevice

    private lateinit var ledgerBleObserver: LedgerBleObserver

    private val receivedDataHandlerListener = object : LedgerDataReceivedCallback.Listener {

        override fun onMtuChanged(mtu: Int) {
            overrideMtu(mtu)
        }

        override fun onDisconnect() {
            disconnect().enqueue()
        }

        override fun onOperationCancelled() {
            ledgerBleObserver.onOperationCancelled()
        }

        override fun onAlgorandAppIsClosed() {
            ledgerBleObserver.onAlgorandAppIsClosed()
        }

        override fun onMissingBytes(device: BluetoothDevice) {
            disconnect().done {
                ledgerBleObserver.onMissingBytes(device)
            }.enqueue()
        }

        override fun onDataReceived(device: BluetoothDevice, data: ByteArray) {
            ledgerBleObserver.onDataReceived(device, data)
        }

        override fun onUnknownError() {
            ledgerBleObserver.onUnknownError()
        }
    }

    private val receivedDataHandler = LedgerDataReceivedCallback(receivedDataHandlerListener)

    override fun setObserver(observer: LedgerBleObserver) {
        connectionObserver = observer
        bondingObserver = observer
        ledgerBleObserver = observer
    }

    override fun isManagerReady(): Boolean = isReady

    override fun getGattCallback(): BleManagerGattCallback {
        return object : BleManagerGattCallback() {
            override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
                gatt.getService(SERVICE_UUID)?.run {
                    characteristicWrite = getCharacteristic(WRITE_CHARACTERISTIC_UUID)
                    characteristicNotify = getCharacteristic(NOTIFY_CHARACTERISTIC_UUID)
                }
                return characteristicWrite != null && characteristicNotify != null
            }

            override fun onServicesInvalidated() {
                characteristicWrite = null
                characteristicNotify = null
            }

            override fun initialize() {
                beginAtomicRequestQueue()
                    .add(
                        requestMtu(GATT_MAX_MTU_SIZE)
                            .with { _, mtu -> log(Log.INFO, "MTU set to $mtu") }
                            .fail { _, status -> log(Log.WARN, "Requested MTU not supported: $status") }
                    )
                    .add(enableNotifications(characteristicNotify))
                    .done { log(Log.INFO, "Target initialized") }
                    .enqueue()
                setNotificationCallback(characteristicNotify)
                    .with(receivedDataHandler)
            }
        }
    }

    override fun log(priority: Int, message: String) {
        super.log(priority, message)
        if (isDebug) {
            Log.println(priority, TAG, message)
        }
    }

    override fun sendPublicKeyRequest(payload: ByteArray) {
        val atomicRequest = beginAtomicRequestQueue()
        packetizeLedgerPayload(payload, mtu).forEach { packet ->
            atomicRequest.add(writeCharacteristic(characteristicWrite, packet, WRITE_TYPE_DEFAULT))
        }
        atomicRequest.enqueue()
    }

    override fun sendVerifyPublicKeyRequest(payload: ByteArray) {
        val atomicRequest = beginAtomicRequestQueue()
        packetizeLedgerPayload(payload, mtu).forEach { packet ->
            atomicRequest.add(writeCharacteristic(characteristicWrite, packet, WRITE_TYPE_DEFAULT))
                .fail { device, status ->
                    ledgerBleObserver.onError(device, "", status)
                }
        }
        atomicRequest.enqueue()
    }

    override fun connectToDevice(bluetoothDevice: BluetoothDevice) {
        connect(bluetoothDevice)
            .timeout(CONNECTION_TIMEOUT)
            .retry(RETRY_COUNT, RETRY_DELAY)
            .fail { _, _ ->
                ledgerBleObserver.onConnectionError()
            }
            .enqueue()
    }

    override fun sendSignTransactionRequest(payload: List<ByteArray>) {
        if (payload.isNotEmpty()) {
            val atomicRequest = beginAtomicRequestQueue()
            payload.forEach { chunkData ->
                packetizeLedgerPayload(chunkData, mtu).forEach { packet ->
                    atomicRequest.add(writeCharacteristic(characteristicWrite, packet, WRITE_TYPE_DEFAULT))
                }
            }
            atomicRequest.enqueue()
        } else {
            log(Log.INFO, "No data is found on the array.")
        }
    }

    override fun isTryingToConnect(): Boolean {
        return (connectionState == STATE_DISCONNECTED || connectionState == STATE_DISCONNECTING).not()
    }

    override fun disconnectFromDevice() {
        disconnect().enqueue()
    }

    companion object {
        private const val SERVICE_KEY = "13D63400-2C97-0004-0000-4C6564676572"
        private const val WRITE_CHARACTERISTIC_KEY = "13D63400-2C97-0004-0002-4C6564676572"
        private const val NOTIFIY_CHARACTERISCTIC_KEY = "13D63400-2C97-0004-0001-4C6564676572"

        val SERVICE_UUID = UUID.fromString(SERVICE_KEY)

        private const val GATT_MAX_MTU_SIZE = 517
        private const val CONNECTION_TIMEOUT = 18000L
        private const val RETRY_COUNT = 0
        private const val RETRY_DELAY = 300

        private val WRITE_CHARACTERISTIC_UUID = UUID.fromString(WRITE_CHARACTERISTIC_KEY)
        private val NOTIFY_CHARACTERISTIC_UUID = UUID.fromString(NOTIFIY_CHARACTERISCTIC_KEY)

        private const val TAG = "LedgerBleManager"
    }
}

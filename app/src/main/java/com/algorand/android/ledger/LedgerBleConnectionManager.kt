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

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import com.algorand.android.BuildConfig
import com.algorand.android.R
import com.algorand.android.utils.getAccountIndexAsByteArray
import com.algorand.android.utils.recordException
import com.algorand.android.utils.removeExcessBytes
import com.algorand.android.utils.shiftOneByteLeft
import java.io.ByteArrayOutputStream
import java.util.UUID
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import no.nordicsemi.android.ble.data.Data

// Use with dagger.
// https://github.com/NordicSemiconductor/Android-BLE-Library/blob/master/MIGRATION.md
class LedgerBleConnectionManager(appContext: Context) : BleManager(appContext) {

    private var characteristicWrite: BluetoothGattCharacteristic? = null
    private var characteristicNotify: BluetoothGattCharacteristic? = null

    private val receivedDataHandler = ReceivedDataHandler()

    private lateinit var ledgerBleObserver: LedgerBleObserver

    fun setObserver(observer: LedgerBleObserver) {
        connectionObserver = observer
        bondingObserver = observer
        ledgerBleObserver = observer
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return object : BleManagerGattCallback() {
            override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
                gatt.getService(NANOX_SERVICE_UUID)?.run {
                    characteristicWrite = getCharacteristic(NANOX_WRITE_CHARACTERISTIC_UUID)
                    characteristicNotify = getCharacteristic(NANOX_NOTIFY_CHARACTERISTIC_UUID)
                }?: gatt.getService(FLEX_SERVICE_UUID)?.run {
                    characteristicWrite = getCharacteristic(FLEX_WRITE_CHARACTERISTIC_UUID)
                    characteristicNotify = getCharacteristic(FLEX_NOTIFY_CHARACTERISTIC_UUID)
                } ?: gatt.getService(STAX_SERVICE_UUID)?.run {
                    characteristicWrite = getCharacteristic(STAX_WRITE_CHARACTERISTIC_UUID)
                    characteristicNotify = getCharacteristic(STAX_NOTIFY_CHARACTERISTIC_UUID)
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
        if (BuildConfig.DEBUG) {
            Log.println(priority, TAG, message)
        }
    }

    inner class ReceivedDataHandler : DataReceivedCallback {
        private var currentSequence = 0
        private var remainingBytes = 0
        private var actionBytesOutputStream = ByteArrayOutputStream()

        override fun onDataReceived(device: BluetoothDevice, data: Data) {
            var offset = 0
            when (data.getByte(offset++)) {
                LEDGER_CLA -> {
                    val mtuValue = data.getByte(MTU_OFFSET)
                    if (mtuValue != null) {
                        overrideMtu(mtuValue.toInt())
                    }
                }
                DATA_CLA -> {
                    val movedSequence = data.getByte(offset++)?.removeExcessBytes()?.shiftOneByteLeft()
                    val sequence = data.getByte(offset++)?.removeExcessBytes()
                    val dataCurrentSequence = sequence?.let { movedSequence?.plus(it) }

                    if (dataCurrentSequence != currentSequence) {
                        resetReceiver("Resetted Receiver $sequence")
                        return
                    }

                    if (currentSequence == 0) {
                        if (data.size() - offset < 2) {
                            resetReceiver("size is lesser than expected.")
                            return
                        }

                        val movedMsgSize = (data.getByte(offset++)!!.removeExcessBytes()).shiftOneByteLeft()
                        val msgSize = data.getByte(offset++)!!.removeExcessBytes()
                        remainingBytes = msgSize + movedMsgSize
                    }

                    val bytesToCopy = data.value?.size?.minus(offset) ?: 0

                    remainingBytes -= bytesToCopy

                    if (bytesToCopy == 0) {
                        resetReceiver("bytes to copy can't be 0.")
                        disconnect().done {
                            ledgerBleObserver.onMissingBytes(device)
                        }.enqueue()
                        return
                    }

                    data.value?.let { actionBytesOutputStream.write(it, offset, bytesToCopy) }

                    when {
                        remainingBytes == 0 -> {
                            handleSuccessfulData(bluetoothDevice, actionBytesOutputStream.toByteArray())
                        }
                        remainingBytes > 0 -> {
                            // wait for the next message
                            currentSequence += 1
                        }
                        remainingBytes < 0 -> {
                            resetReceiver("Minus byte is remaining. Something is wrong. $remainingBytes")
                        }
                    }
                }
            }
        }

        private fun resetReceiver(message: String) {
            currentSequence = 0
            remainingBytes = 0
            actionBytesOutputStream = ByteArrayOutputStream()
            log(Log.INFO, message)
        }

        private fun handleSuccessfulData(ledgerDevice: BluetoothDevice?, data: ByteArray) {
            resetReceiver("Successfully read all.")
            if (ledgerDevice == null) {
                return
            }
            when {
                data.size == ERROR_DATA_SIZE -> {
                    when {
                        OPERATION_CANCELLED_CODES.any { data.contentEquals(it) } -> {
                            ledgerBleObserver.onOperationCancelled()
                        }
                        data.contentEquals(NEXT_PAGE_CODE) -> return
                        else -> {
                            disconnect().enqueue()
                            ledgerBleObserver.onManagerError(
                                R.string.error_app_closed_message,
                                R.string.error_app_closed_title
                            )
                        }
                    }
                }
                data.size > ERROR_DATA_SIZE -> {
                    ledgerBleObserver.onDataReceived(
                        device = ledgerDevice,
                        data = data.dropLast(RETURN_CODE_BYTE_COUNT).toByteArray()
                    )
                }
                else -> {
                    recordException(
                        Exception("LedgerBleConnectionManager::handleSuccessfulData:: {data.size} is lower than 2")
                    )
                    disconnect().enqueue()
                    ledgerBleObserver.onManagerError(R.string.unknown_error)
                }
            }
        }
    }

    fun sendPublicKeyRequest(index: Int) {
        val atomicRequest = beginAtomicRequestQueue()
        val publicKeyRequestInstruction = PUBLIC_KEY_WITH_INDEX + getAccountIndexAsByteArray(index)
        packetizeData(publicKeyRequestInstruction).forEach { packet ->
            atomicRequest.add(writeCharacteristic(characteristicWrite, packet, WRITE_TYPE_DEFAULT))
        }
        atomicRequest.enqueue()
    }

    fun sendVerifyPublicKeyRequest(index: Int) {
        val atomicRequest = beginAtomicRequestQueue()
        val verifyPublicKeyRequestInstruction = VERIFY_PUBLIC_KEY_WITH_INDEX + getAccountIndexAsByteArray(index)
        packetizeData(verifyPublicKeyRequestInstruction).forEach { packet ->
            atomicRequest.add(writeCharacteristic(characteristicWrite, packet, WRITE_TYPE_DEFAULT))
                .fail { device, status ->
                    ledgerBleObserver.onError(device, "", status)
                }
        }
        atomicRequest.enqueue()
    }

    fun connectToDevice(bluetoothDevice: BluetoothDevice) {
        connect(bluetoothDevice)
            .timeout(CONNECTION_TIMEOUT)
            .retry(RETRY_COUNT, RETRY_DELAY)
            .fail { _, _ ->
                ledgerBleObserver.onManagerError(R.string.error_connection_message, R.string.error_connection_title)
            }
            .enqueue()
    }

    fun sendSignTransactionRequest(transactionData: ByteArray, accountIndex: Int) {
        val output = mutableListOf<ByteArray>()
        var bytesRemaining = transactionData.size + ACCOUNT_INDEX_DATA_SIZE
        var offset = 0
        var p1 = P1_FIRST_WITH_ACCOUNT
        var p2 = P2_MORE

        while (bytesRemaining > 0) {
            val bytesRemainingWithHeader = bytesRemaining + HEADER_SIZE

            val packetSize: Int = if (bytesRemainingWithHeader <= CHUNK_SIZE) bytesRemainingWithHeader else CHUNK_SIZE

            val packet = ByteArrayOutputStream()

            val remainingSpaceInPacket = packetSize - HEADER_SIZE

            var bytesToCopyLength =
                if (remainingSpaceInPacket < bytesRemaining) remainingSpaceInPacket else bytesRemaining

            bytesRemaining -= bytesToCopyLength

            if (bytesRemaining == 0) {
                p2 = P2_LAST
            }

            with(packet) {
                write(ALGORAND_CLA)
                write(SIGN_INS)
                write(p1)
                write(p2)
                write(bytesToCopyLength)
                if (p1 == P1_FIRST_WITH_ACCOUNT) {
                    write(getAccountIndexAsByteArray(accountIndex))
                    bytesToCopyLength -= ACCOUNT_INDEX_DATA_SIZE
                }
                write(transactionData, offset, bytesToCopyLength)
                offset += bytesToCopyLength
            }

            p1 = P1_MORE

            output.add(packet.toByteArray())
            log(Log.INFO, packet.toByteArray().toString())
        }

        if (output.isNotEmpty()) {
            val atomicRequest = beginAtomicRequestQueue()
            output.forEach { chunkData ->
                packetizeData(chunkData).forEach { packet ->
                    atomicRequest.add(writeCharacteristic(characteristicWrite, packet, WRITE_TYPE_DEFAULT))
                }
            }
            atomicRequest.enqueue()
        } else {
            log(Log.INFO, "No data is found on the array.")
        }
    }

    // Every data must be sent as packetized.
    private fun packetizeData(msg: ByteArray): List<ByteArray> {
        val out = mutableListOf<ByteArray>()
        var sequenceIdx = 0
        var offset = 0
        var first = true
        var remainingBytes = msg.size

        while (remainingBytes > 0) {
            val byteArrayOutputStream = ByteArrayOutputStream()

            // 0x05 Marks application specific data
            byteArrayOutputStream.write(DATA_CLA.toInt())

            // Encode sequence number
            byteArrayOutputStream.write(sequenceIdx.shiftOneByteLeft())
            byteArrayOutputStream.write(sequenceIdx.removeExcessBytes())

            // If this is the first packet, also encode the total message length
            if (first) {
                byteArrayOutputStream.write(msg.size.shiftOneByteLeft())
                byteArrayOutputStream.write(msg.size.removeExcessBytes())
                first = false
            }

            // Copy some number of bytes into the packet
            val remainingSpaceInPacket = mtu - byteArrayOutputStream.size() - CONSTANT_BYTE_COUNT
            val bytesToCopy = if (remainingSpaceInPacket < remainingBytes) {
                remainingSpaceInPacket
            } else {
                remainingBytes
            }

            remainingBytes -= bytesToCopy

            byteArrayOutputStream.write(msg, offset, bytesToCopy)

            sequenceIdx += 1
            offset += bytesToCopy

            out.add(byteArrayOutputStream.toByteArray())
        }
        return out
    }

    fun isTryingToConnect(): Boolean {
        return (
                connectionState == BluetoothProfile.STATE_DISCONNECTED ||
                        connectionState == BluetoothProfile.STATE_DISCONNECTING
                ).not()
    }

    companion object {
        private const val NANOX_SERVICE_KEY = "13D63400-2C97-0004-0000-4C6564676572"
        private const val NANOX_WRITE_CHARACTERISTIC_KEY = "13D63400-2C97-0004-0002-4C6564676572"
        private const val NANOX_NOTIFY_CHARACTERISTIC_KEY = "13D63400-2C97-0004-0001-4C6564676572"

        private const val STAX_SERVICE_KEY = "13d63400-2c97-6004-0000-4c6564676572"
        private const val STAX_WRITE_CHARACTERISTIC_KEY = "13d63400-2c97-6004-0002-4c6564676572"
        private const val STAX_NOTIFY_CHARACTERISTIC_KEY = "13d63400-2c97-6004-0001-4c6564676572"

        private const val FLEX_SERVICE_KEY = "13d63400-2c97-3004-0000-4c6564676572"
        private const val FLEX_WRITE_CHARACTERISTIC_KEY = "13d63400-2c97-3004-0002-4c6564676572"
        private const val FLEX_NOTIFY_CHARACTERISTIC_KEY = "13d63400-2c97-3004-0001-4c6564676572"

        val NANOX_SERVICE_UUID: UUID = UUID.fromString(NANOX_SERVICE_KEY)
        private val NANOX_WRITE_CHARACTERISTIC_UUID = UUID.fromString(NANOX_WRITE_CHARACTERISTIC_KEY)
        private val NANOX_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString(NANOX_NOTIFY_CHARACTERISTIC_KEY)

        val STAX_SERVICE_UUID: UUID = UUID.fromString(STAX_SERVICE_KEY)
        private val STAX_WRITE_CHARACTERISTIC_UUID = UUID.fromString(STAX_WRITE_CHARACTERISTIC_KEY)
        private val STAX_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString(STAX_NOTIFY_CHARACTERISTIC_KEY)

        val FLEX_SERVICE_UUID: UUID = UUID.fromString(FLEX_SERVICE_KEY)
        private val FLEX_WRITE_CHARACTERISTIC_UUID = UUID.fromString(FLEX_WRITE_CHARACTERISTIC_KEY)
        private val FLEX_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString(FLEX_NOTIFY_CHARACTERISTIC_KEY)

        private const val GATT_MAX_MTU_SIZE = 517
        private const val CONNECTION_TIMEOUT = 18000L
        private const val RETRY_COUNT = 0
        private const val RETRY_DELAY = 300

        private const val RETURN_CODE_BYTE_COUNT = 2

        private const val MTU_OFFSET = 5

        private const val LEDGER_CLA: Byte = 0x08
        private const val DATA_CLA: Byte = 0x05

        private const val ALGORAND_CLA = 0x80
        private const val PUBLIC_KEY_INS = 0x03
        private const val SIGN_INS = 0x08

        private const val P1_FIRST_WITH_ACCOUNT = 0x01
        private const val P1_FIRST = 0x00
        private const val P1_MORE = 0x80
        private const val P2_LAST = 0x00
        private const val P2_MORE = 0x80

        const val ACCOUNT_INDEX_DATA_SIZE = 0x04

        // this need one more byte which is index of the account.
        // toByte is evaluated at compile time so, it's ok this way.
        private val PUBLIC_KEY_WITH_INDEX = byteArrayOf(
            ALGORAND_CLA.toByte(),
            PUBLIC_KEY_INS.toByte(),
            P1_FIRST.toByte(),
            P2_LAST.toByte(),
            ACCOUNT_INDEX_DATA_SIZE.toByte(),
        )

        private val VERIFY_PUBLIC_KEY_WITH_INDEX = byteArrayOf(
            ALGORAND_CLA.toByte(),
            PUBLIC_KEY_INS.toByte(),
            P1_MORE.toByte(),
            P2_LAST.toByte(),
            ACCOUNT_INDEX_DATA_SIZE.toByte()
        )

        private const val CHUNK_SIZE = 0xFF
        private const val HEADER_SIZE = 5

        private const val CONSTANT_BYTE_COUNT = 3

        private const val ERROR_DATA_SIZE = 2

        /**
         * Ledger cancellation error codes;
         * Before version v2.0.7 -> 0x6985
         * v2.0.7 -> 0x6986
         */
        private val OPERATION_CANCELLED_CODES = listOf(
            byteArrayOf(0x69, 0x85.toByte()),
            byteArrayOf(0x69, 0x86.toByte())
        )
        private val NEXT_PAGE_CODE = byteArrayOf(0x90.toByte(), 0x00.toByte())

        private const val TAG = "LedgerBleManager"
    }
}

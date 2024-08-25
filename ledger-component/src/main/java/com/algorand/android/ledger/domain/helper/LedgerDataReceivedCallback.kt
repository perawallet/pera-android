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

import android.bluetooth.BluetoothDevice
import android.util.Log
import com.algorand.android.ledger.LedgerConstants.DATA_CLA
import com.algorand.android.ledger.LedgerConstants.LEDGER_CLA
import com.algorand.android.ledger.LedgerConstants.MTU_OFFSET
import com.algorand.android.ledger.domain.removeExcessBytes
import com.algorand.android.ledger.domain.shiftOneByteLeft
import java.io.ByteArrayOutputStream
import no.nordicsemi.android.ble.callback.DataReceivedCallback
import no.nordicsemi.android.ble.data.Data

internal class LedgerDataReceivedCallback(private val listener: Listener) : DataReceivedCallback {

    private var currentSequence = 0
    private var remainingBytes = 0
    private var actionBytesOutputStream = ByteArrayOutputStream()

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        var offset = 0
        when (data.getByte(offset++)) {
            LEDGER_CLA -> {
                val mtuValue = data.getByte(MTU_OFFSET)
                if (mtuValue != null) {
                    listener.onMtuChanged(mtuValue.toInt())
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
                    listener.onMissingBytes(device)
                    return
                }

                data.value?.let { actionBytesOutputStream.write(it, offset, bytesToCopy) }

                when {
                    remainingBytes == 0 -> {
                        val receivedData = actionBytesOutputStream.toByteArray()
                            .dropLast(RETURN_CODE_BYTE_COUNT)
                            .toByteArray()
                        listener.onDataReceived(device, receivedData)
                        resetReceiver("")
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
        Log.i("LedgerDataReceivedCallback", message)
    }

    interface Listener {
        fun onMtuChanged(mtu: Int)
        fun onDisconnect()
        fun onDataReceived(device: BluetoothDevice, data: ByteArray)
        fun onOperationCancelled()
        fun onMissingBytes(device: BluetoothDevice)
        fun onUnknownError()
        fun onAlgorandAppIsClosed()
    }

    private companion object {
        const val RETURN_CODE_BYTE_COUNT = 2
    }
}

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

import com.algorand.android.module.ledger.LedgerConstants.CONSTANT_BYTE_COUNT
import com.algorand.android.module.ledger.LedgerConstants.DATA_CLA
import com.algorand.android.module.ledger.domain.removeExcessBytes
import com.algorand.android.module.ledger.domain.shiftOneByteLeft
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal class PacketizeLedgerPayloadImpl @Inject constructor() : PacketizeLedgerPayload {

    override fun invoke(message: ByteArray, mtu: Int): List<ByteArray> {
        val out = mutableListOf<ByteArray>()
        var sequenceIdx = 0
        var offset = 0
        var first = true
        var remainingBytes = message.size

        while (remainingBytes > 0) {
            val byteArrayOutputStream = ByteArrayOutputStream()

            // 0x05 Marks application specific data
            byteArrayOutputStream.write(DATA_CLA.toInt())

            // Encode sequence number
            byteArrayOutputStream.write(sequenceIdx.shiftOneByteLeft())
            byteArrayOutputStream.write(sequenceIdx.removeExcessBytes())

            // If this is the first packet, also encode the total message length
            if (first) {
                byteArrayOutputStream.write(message.size.shiftOneByteLeft())
                byteArrayOutputStream.write(message.size.removeExcessBytes())
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

            byteArrayOutputStream.write(message, offset, bytesToCopy)

            sequenceIdx += 1
            offset += bytesToCopy

            out.add(byteArrayOutputStream.toByteArray())
        }
        return out
    }
}

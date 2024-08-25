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

package com.algorand.android.ledger.domain.payload

import com.algorand.android.ledger.LedgerConstants.ACCOUNT_INDEX_DATA_SIZE
import com.algorand.android.ledger.LedgerConstants.ALGORAND_CLA
import com.algorand.android.ledger.LedgerConstants.CHUNK_SIZE
import com.algorand.android.ledger.LedgerConstants.HEADER_SIZE
import com.algorand.android.ledger.LedgerConstants.P1_FIRST_WITH_ACCOUNT
import com.algorand.android.ledger.LedgerConstants.P1_MORE
import com.algorand.android.ledger.LedgerConstants.P2_LAST
import com.algorand.android.ledger.LedgerConstants.P2_MORE
import com.algorand.android.ledger.LedgerConstants.SIGN_INS
import com.algorand.android.ledger.domain.getAccountIndexAsByteArray
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal class CreateSignTransactionRequestPayloadImpl @Inject constructor() : CreateSignTransactionRequestPayload {

    override fun invoke(transactionData: ByteArray, accountIndex: Int): List<ByteArray> {

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
            // log(Log.INFO, packet.toByteArray().toString())
        }
        return output
    }
}

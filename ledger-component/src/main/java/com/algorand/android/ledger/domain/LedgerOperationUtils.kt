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

package com.algorand.android.ledger.domain

import com.algorand.android.ledger.LedgerConstants.ACCOUNT_INDEX_DATA_SIZE

private const val CHUNK_SIZE = 0xFF
private const val BIT_COUNT = 8

internal fun Int.removeExcessBytes(): Int {
    return this and CHUNK_SIZE
}

internal fun Byte.removeExcessBytes(): Int {
    return this.toInt() and CHUNK_SIZE
}

internal fun Int.shiftOneByteLeft(): Int {
    return this shl BIT_COUNT
}

// byteArrayOf(10, 2, 15, 11) -> 0A020F0B
internal fun ByteArray.toHexString(): String {
    val hexStringBuilder = StringBuilder()
    forEach { byte ->
        hexStringBuilder.append(String.format("%02X", byte))
    }
    return hexStringBuilder.toString()
}

internal fun getAccountIndexAsByteArray(accountIndex: Int): ByteArray {
    return mutableListOf<Byte>().apply {
        for (i in ACCOUNT_INDEX_DATA_SIZE - 1 downTo 0) {
            add(accountIndex.shr(i * Byte.Companion.SIZE_BITS).toByte())
        }
    }.toByteArray()
}

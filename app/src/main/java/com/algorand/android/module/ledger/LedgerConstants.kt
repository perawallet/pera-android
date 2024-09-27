@file:Suppress("MagicNumber")
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

package com.algorand.android.module.ledger

internal object LedgerConstants {
    const val LEDGER_CLA: Byte = 0x08
    const val DATA_CLA: Byte = 0x05
    const val MTU_OFFSET = 5
    val NEXT_PAGE_CODE = byteArrayOf(0x90.toByte(), 0x00.toByte())
    const val ERROR_DATA_SIZE = 2
    const val RETURN_CODE_BYTE_COUNT = 2
    const val CONSTANT_BYTE_COUNT = 3

    const val P1_FIRST = 0x00
    const val P1_FIRST_WITH_ACCOUNT = 0x01
    const val P2_MORE = 0x80
    const val P2_LAST = 0x00
    const val P1_MORE = 0x80
    const val ALGORAND_CLA = 0x80
    const val PUBLIC_KEY_INS = 0x03
    const val SIGN_INS = 0x08

    const val ACCOUNT_INDEX_DATA_SIZE = 0x04

    const val CHUNK_SIZE = 0xFF
    const val HEADER_SIZE = 5

    /**
     * Ledger cancellation error codes;
     * Before version v2.0.7 -> 0x6985
     * v2.0.7 -> 0x6986
     */
    val OPERATION_CANCELLED_CODES = listOf(
        byteArrayOf(0x69, 0x85.toByte()),
        byteArrayOf(0x69, 0x86.toByte())
    )
}

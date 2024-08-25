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
import com.algorand.android.ledger.LedgerConstants.P1_MORE
import com.algorand.android.ledger.LedgerConstants.P2_LAST
import com.algorand.android.ledger.LedgerConstants.PUBLIC_KEY_INS
import com.algorand.android.ledger.domain.getAccountIndexAsByteArray
import javax.inject.Inject

internal class CreateVerifyPublicKeyRequestPayloadImpl @Inject constructor() : CreateVerifyPublicKeyRequestPayload {

    override fun invoke(accountIndex: Int): ByteArray {
        return VERIFY_PUBLIC_KEY_WITH_INDEX + getAccountIndexAsByteArray(accountIndex)
    }

    private companion object {
        val VERIFY_PUBLIC_KEY_WITH_INDEX = byteArrayOf(
            ALGORAND_CLA.toByte(),
            PUBLIC_KEY_INS.toByte(),
            P1_MORE.toByte(),
            P2_LAST.toByte(),
            ACCOUNT_INDEX_DATA_SIZE.toByte()
        )
    }
}

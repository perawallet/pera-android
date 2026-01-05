/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.algosdk.transaction.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RawTransactionTest {

    @Test
    fun `EXPECT true WHEN transaction rekeys any of the addresses`() {
        val rekeyTxn = EMPTY_TRANSACTION.copy(
            senderAddress = AlgorandAddress(ADDRESS_1, null),
            rekeyAddress = AlgorandAddress("some address", null)
        )

        val result = rekeyTxn isRekeyingAddressesIn listOf(ADDRESS_1)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN transaction does not rekey any of the addresses`() {
        val rekeyTxn = EMPTY_TRANSACTION.copy(
            senderAddress = AlgorandAddress("not local address", null),
            rekeyAddress = AlgorandAddress("random address", null)
        )

        val result = rekeyTxn isRekeyingAddressesIn listOf(ADDRESS_1)

        assertFalse(result)
    }

    private companion object {
        const val ADDRESS_1 = "ADDRESS_1"

        val EMPTY_TRANSACTION = RawTransaction(
            amount = null,
            fee = null,
            firstValidRound = null,
            genesisId = null,
            genesisHash = null,
            lastValidRound = null,
            note = null,
            receiverAddress = null,
            senderAddress = null,
            transactionType = RawTransactionType.UNDEFINED,
            closeToAddress = null,
            rekeyAddress = null,
            assetCloseToAddress = null,
            assetReceiverAddress = null,
            assetAmount = null,
            assetId = null,
            appArgs = null,
            appOnComplete = null,
            appId = null,
            appGlobalSchema = null,
            appLocalSchema = null,
            appExtraPages = null,
            approvalHash = null,
            stateHash = null,
            assetIdBeingConfigured = null,
            assetConfigParameters = null,
            groupId = null,
            innerTransactions = null,
            rejectVersion = null,
            accessListSize = null
        )
    }
}

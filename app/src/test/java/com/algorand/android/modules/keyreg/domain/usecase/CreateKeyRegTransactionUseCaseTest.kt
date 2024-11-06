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

package com.algorand.android.modules.keyreg.domain.usecase

import com.algorand.android.models.Result
import com.algorand.android.models.TransactionParams
import com.algorand.android.modules.accounts.domain.usecase.GetAuthAddressOfAnAccount
import com.algorand.android.modules.accounts.domain.usecase.IsSenderRekeyedToAnotherAccount
import com.algorand.android.modules.algosdk.domain.model.OnlineKeyRegTransactionPayload
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOfflineTransaction
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOnlineTransaction
import com.algorand.android.modules.keyreg.domain.model.KeyRegTransaction
import com.algorand.android.modules.keyreg.ui.presentation.model.KeyRegTransactionDetail
import com.algorand.android.modules.transaction.domain.GetTransactionParams
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CreateKeyRegTransactionUseCaseTest {

    private val isSenderRekeyedToAnotherAccount: IsSenderRekeyedToAnotherAccount = mockk {
        coEvery { this@mockk(ACCOUNT_ADDRESS) } returns false
    }
    private val getAuthAddressOfAnAccount: GetAuthAddressOfAnAccount = mockk {
        coEvery { this@mockk(ACCOUNT_ADDRESS) } returns null
    }
    private val getTransactionParams: GetTransactionParams = mockk()
    private val buildKeyRegOnlineTransaction: BuildKeyRegOnlineTransaction = mockk()
    private val buildKeyRegOfflineTransaction: BuildKeyRegOfflineTransaction = mockk()

    private val sut = CreateKeyRegTransactionUseCase(
        isSenderRekeyedToAnotherAccount,
        getAuthAddressOfAnAccount,
        getTransactionParams,
        buildKeyRegOfflineTransaction,
        buildKeyRegOnlineTransaction
    )

    @Test
    fun `EXPECT error WHEN transaction params returns error`() = runTest {
        coEvery { getTransactionParams() } returns Result.Error(IllegalArgumentException())

        val result = sut(ONLINE_KEY_REG_TXN_DETAIL)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `EXPECT error WHEN transaction byte array is null`() = runTest {
        coEvery { getTransactionParams() } returns Result.Success(TRANSACTION_PARAMS)
        coEvery { buildKeyRegOnlineTransaction(ONLINE_TXN_PAYLOAD) } returns null

        val result = sut(ONLINE_KEY_REG_TXN_DETAIL)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `EXPECT online txn array WHEN payload is for online txn`() = runTest {
        val onlineTxnByteArray = "txnByteArray".toByteArray()
        coEvery { getTransactionParams() } returns Result.Success(TRANSACTION_PARAMS)
        coEvery { buildKeyRegOnlineTransaction(ONLINE_TXN_PAYLOAD) } returns onlineTxnByteArray

        val result = sut(ONLINE_KEY_REG_TXN_DETAIL)

        val expected = Result.Success(KeyRegTransaction(onlineTxnByteArray, ACCOUNT_ADDRESS, null, false))
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT offline txn array WHEN payload is for offline txn`() = runTest {
        val offlineTxnByteArray = "txnByteArray".toByteArray()
        coEvery { getTransactionParams() } returns Result.Success(TRANSACTION_PARAMS)
        coEvery { buildKeyRegOfflineTransaction(ACCOUNT_ADDRESS, TRANSACTION_PARAMS) } returns offlineTxnByteArray

        val result = sut(OFFLINE_KEY_REG_TXN_DETAIL)

        val expected = Result.Success(KeyRegTransaction(offlineTxnByteArray, ACCOUNT_ADDRESS, null, false))
        assertEquals(expected, result)
    }

    private companion object {
        const val ACCOUNT_ADDRESS = "address"
        val OFFLINE_KEY_REG_TXN_DETAIL = KeyRegTransactionDetail(
            address = ACCOUNT_ADDRESS,
            type = "type",
            voteKey = null,
            selectionPublicKey = null,
            sprfkey = "sprfkey",
            voteFirstRound = null,
            voteLastRound = null,
            voteKeyDilution = null,
            fee = null,
            note = "note",
            xnote = "xnote"
        )
        val ONLINE_KEY_REG_TXN_DETAIL = KeyRegTransactionDetail(
            address = "address",
            type = "type",
            voteKey = "voteKey",
            selectionPublicKey = "selectionPublicKey",
            sprfkey = "sprfkey",
            voteFirstRound = "1",
            voteLastRound = "2",
            voteKeyDilution = "3",
            fee = null,
            note = "note",
            xnote = "xnote"
        )
        val TRANSACTION_PARAMS = peraFixture<TransactionParams>()
        val ONLINE_TXN_PAYLOAD = OnlineKeyRegTransactionPayload(
            senderAddress = ONLINE_KEY_REG_TXN_DETAIL.address,
            selectionPublicKey = ONLINE_KEY_REG_TXN_DETAIL.selectionPublicKey.orEmpty(),
            voteKey = ONLINE_KEY_REG_TXN_DETAIL.voteKey.orEmpty(),
            voteFirstRound = ONLINE_KEY_REG_TXN_DETAIL.voteFirstRound.orEmpty(),
            voteLastRound = ONLINE_KEY_REG_TXN_DETAIL.voteLastRound.orEmpty(),
            voteKeyDilution = ONLINE_KEY_REG_TXN_DETAIL.voteKeyDilution.orEmpty(),
            txnParams = TRANSACTION_PARAMS
        )
    }
}

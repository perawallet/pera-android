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

package com.algorand.wallet.transaction.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.transaction.domain.model.TransactionId
import com.algorand.wallet.transaction.domain.repository.TransactionRepository
import io.mockk.coEvery

import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SendSignedTransactionUseCaseTest {

    private val transactionRepository: TransactionRepository = mockk(relaxed = true)

    private val sut = SendSignedTransactionUseCase(transactionRepository)

    @Test
    fun `EXPECT error WHEN sending transaction fails`(): TestResult = runTest {
        coEvery { transactionRepository.sendSignedTransaction(TXN_BYTE_ARRAY) } returns PeraResult.Error(Exception())

        val result = sut(TXN_BYTE_ARRAY, WAIT_FOR_CONFIRMATION)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN waiting is required and it fails`(): TestResult = runTest {
        coEvery { transactionRepository.sendSignedTransaction(TXN_BYTE_ARRAY) } returns PeraResult.Success(TXN_ID)
        coEvery { transactionRepository.waitForConfirmation(TXN_ID, MAX_ROUND) } returns PeraResult.Error(Exception())

        val result = sut(TXN_BYTE_ARRAY, WAIT_FOR_CONFIRMATION)

        assert(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT success WHEN waiting is not required and sending transaction is successful`(): TestResult = runTest {
        coEvery { transactionRepository.sendSignedTransaction(TXN_BYTE_ARRAY) } returns PeraResult.Success(TXN_ID)

        val result = sut(TXN_BYTE_ARRAY, waitForConfirmation = false)

        assertEquals(result.getDataOrNull() ?: return@runTest, TXN_ID)
    }

    @Test
    fun `EXPECT success WHEN waiting is required and sending transaction and waiting are successful`(): TestResult =
        runTest {
            coEvery { transactionRepository.sendSignedTransaction(TXN_BYTE_ARRAY) } returns PeraResult.Success(TXN_ID)
            coEvery { transactionRepository.waitForConfirmation(TXN_ID, MAX_ROUND) } returns PeraResult.Success(TXN_ID)
            val result = sut(TXN_BYTE_ARRAY, WAIT_FOR_CONFIRMATION)
            assertEquals(result.getDataOrNull() ?: return@runTest, TXN_ID)
        }

    private companion object {
        val TXN_BYTE_ARRAY = byteArrayOf(1, 2, 3)
        val TXN_ID = peraFixture<TransactionId>()
        const val MAX_ROUND = 3
        const val WAIT_FOR_CONFIRMATION = true
    }
}

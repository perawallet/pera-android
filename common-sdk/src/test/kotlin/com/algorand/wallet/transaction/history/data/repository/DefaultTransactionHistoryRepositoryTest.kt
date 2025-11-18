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

package com.algorand.wallet.transaction.history.data.repository

import androidx.paging.PagingSource
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.transaction.history.data.mapper.TransactionHistorySwapGroupDetailMapper
import com.algorand.wallet.transaction.history.data.model.TransactionHistorySwapGroupDetailResponse
import com.algorand.wallet.transaction.history.data.service.TransactionHistoryApiService
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistoryPagingData
import com.algorand.wallet.transaction.history.domain.model.TransactionHistorySwapGroupDetail
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal.ZERO
import java.time.ZonedDateTime

class DefaultTransactionHistoryRepositoryTest {

    private val pagingSource: PagingSource<TransactionHistoryPagingData, TransactionHistory> = mockk()
    private val apiService: TransactionHistoryApiService = mockk()
    private val swapGroupDetailMapper: TransactionHistorySwapGroupDetailMapper = mockk()
    private val errorLogger: PeraErrorLogger = mockk()

    private val sut = DefaultTransactionHistoryRepository(pagingSource, apiService, swapGroupDetailMapper, errorLogger)

    @Test
    fun `EXPECT error WHEN swap group transaction api call throws exception`(): TestResult = runTest {
        coEvery { apiService.getSwapGroupTransactions(ADDRESS, GROUP_ID) } throws Exception()

        val result = sut.getSwapGroupTransactions(ADDRESS, GROUP_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error and error to be logged WHEN response can not be mapped to domain model`(): TestResult = runTest {
        coEvery { apiService.getSwapGroupTransactions(ADDRESS, GROUP_ID) } returns SWAP_GROUP_DETAIL_RESPONSE
        every { swapGroupDetailMapper(ADDRESS, SWAP_GROUP_DETAIL_RESPONSE) } returns null

        val result = sut.getSwapGroupTransactions(ADDRESS, GROUP_ID)

        assertTrue(result is PeraResult.Error)
        verify { errorLogger.logError(any<String>()) }
    }

    @Test
    fun `EXPECT swap group detail WHEN api call and mapping succeeds`(): TestResult = runTest {
        coEvery { apiService.getSwapGroupTransactions(ADDRESS, GROUP_ID) } returns SWAP_GROUP_DETAIL_RESPONSE
        every { swapGroupDetailMapper(ADDRESS, SWAP_GROUP_DETAIL_RESPONSE) } returns SWAP_GROUP_DETAIL

        val result = sut.getSwapGroupTransactions(ADDRESS, GROUP_ID)

        assertEquals(PeraResult.Success(SWAP_GROUP_DETAIL), result)
    }

    private companion object {
        const val ADDRESS = "address"
        const val GROUP_ID = "groupId"
        val SWAP_GROUP_DETAIL_RESPONSE = TransactionHistorySwapGroupDetailResponse(
            "", "", "", null, null, "", "", 0, null, "", null, null
        )
        val SWAP_GROUP_DETAIL = TransactionHistorySwapGroupDetail(
            "", "", "", "", 1L, "", ZERO, 2L, "", ZERO, emptyList(), null, ZonedDateTime.now()
        )
    }
}

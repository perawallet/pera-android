/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.modules.accountcore.domain.model.AccountTotalValue
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.test.peraFixture
import com.algorand.test.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Test

class GetAccountTotalValueFlowUseCaseTest {

    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow = mockk()
    private val getAccountTotalValue: GetAccountTotalValue = mockk()

    private val sut = GetAccountTotalValueFlowUseCase(getAccountLiteCacheFlow, getAccountTotalValue)

    @Test
    fun `EXPECT nothing WHEN account lite is null`() {
        every { getAccountLiteCacheFlow() } returns MutableStateFlow<AccountLiteCacheStatus>(
            AccountLiteCacheStatus.Data(emptyList(), emptyMap())
        )

        val result = sut(ADDRESS, false).test()

        coVerify(exactly = 0) { getAccountTotalValue(address = any(), includeAlgo = any()) }
        result.assertNoValue()
    }

    @Test
    fun `EXPECT updated total value WHEN account information is updated`() {
        val accountLiteCacheFlow = MutableStateFlow(ACCOUNT_LITE_CACHE)
        every { getAccountLiteCacheFlow() } returns accountLiteCacheFlow
        coEvery { getAccountTotalValue(ACCOUNT_LITE, false) } returns TOTAL_VALUE
        coEvery { getAccountTotalValue(UPDATED_ACCOUNT_LITE, false) } returns UPDATED_TOTAL_VALUE

        val result = sut(ADDRESS, false).test()
        accountLiteCacheFlow.update { AccountLiteCacheStatus.Data(emptyList(), mapOf(ADDRESS to UPDATED_ACCOUNT_LITE)) }

        result.assertValueHistory(
            TOTAL_VALUE,
            UPDATED_TOTAL_VALUE
        )
    }

    private companion object {
        const val ADDRESS = "address"
        val ACCOUNT_LITE = peraFixture<AccountLite>().copy(
            address = ADDRESS,
            cachedInfo = peraFixture()
        )
        val UPDATED_ACCOUNT_LITE = peraFixture<AccountLite>()
        val ACCOUNT_LITE_CACHE = AccountLiteCacheStatus.Data(
            accountLites = mapOf(ADDRESS to ACCOUNT_LITE),
            localAccounts = emptyList()
        )
        val TOTAL_VALUE = peraFixture<AccountTotalValue>()
        val UPDATED_TOTAL_VALUE = peraFixture<AccountTotalValue>()
    }
}

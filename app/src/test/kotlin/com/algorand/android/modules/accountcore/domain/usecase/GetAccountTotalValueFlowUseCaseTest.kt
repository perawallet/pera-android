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

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.modules.accountcore.domain.model.AccountTotalValue
import com.algorand.test.peraFixture
import com.algorand.test.test
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformationFlow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import org.junit.Test

class GetAccountTotalValueFlowUseCaseTest {

    private val getAccountInformationFlow: GetAccountInformationFlow = mockk()
    private val getAccountTotalValue: GetAccountTotalValue = mockk()

    private val sut = GetAccountTotalValueFlowUseCase(getAccountInformationFlow, getAccountTotalValue)

    @Test
    fun `EXPECT nothing WHEN account information is null`() {
        every { getAccountInformationFlow(ADDRESS) } returns flowOf(null)

        val result = sut(ADDRESS, false).test()

        coVerify(exactly = 0) { getAccountTotalValue(accountInformation = any(), includeAlgo = any()) }
        result.assertNoValue()
    }

    @Test
    fun `EXPECT updated total value WHEN account information is updated`() {
        val accountInformationFlow = MutableStateFlow(ACCOUNT_INFO)
        every { getAccountInformationFlow(ADDRESS) } returns accountInformationFlow
        coEvery { getAccountTotalValue(ACCOUNT_INFO, false) } returns TOTAL_VALUE
        coEvery { getAccountTotalValue(UPDATED_ACCOUNT_INFO, false) } returns UPDATED_TOTAL_VALUE

        val result = sut(ADDRESS, false).test()
        accountInformationFlow.update { UPDATED_ACCOUNT_INFO }

        result.assertValueHistory(
            TOTAL_VALUE,
            UPDATED_TOTAL_VALUE
        )
    }

    private companion object {
        const val ADDRESS = "address"
        val ACCOUNT_INFO = peraFixture<AccountInformation>()
        val UPDATED_ACCOUNT_INFO = peraFixture<AccountInformation>()
        val TOTAL_VALUE = peraFixture<AccountTotalValue>()
        val UPDATED_TOTAL_VALUE = peraFixture<AccountTotalValue>()
    }
}

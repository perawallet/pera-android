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

package com.algorand.wallet.asb.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAsbEligibleAccountsUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mockk()

    private val sut = GetAsbEligibleAccountsUseCase(getLocalAccounts)

    @Test
    fun `EXPECT asb eligible account addresses`() = runTest {
        val localAccounts = listOf(ALGO_25, NO_AUTH, LEDGER_BLE, HD_KEY)
        coEvery { getLocalAccounts() } returns localAccounts

        val result = sut()

        val expected = listOf(ALGO_25, NO_AUTH)
        assertEquals(expected, result)
    }

    private companion object {
        val ALGO_25 = peraFixture<LocalAccount.Algo25>()
        val NO_AUTH = peraFixture<LocalAccount.NoAuth>()
        val LEDGER_BLE = peraFixture<LocalAccount.LedgerBle>()
        val HD_KEY = peraFixture<LocalAccount.HdKey>()
    }
}

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

package com.algorand.common.account.local.usecase

import com.algorand.common.account.local.domain.usecase.*
import com.algorand.common.account.local.domain.usecase.IsThereAnyAccountWithAddressUseCase
import com.algorand.common.account.local.domain.model.LocalAccount.Algo25
import com.algorand.common.account.local.domain.model.LocalAccount.LedgerBle
import com.algorand.common.account.local.domain.model.LocalAccount.NoAuth
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsThereAnyAccountWithAddressUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mockk()

    private val sut = IsThereAnyAccountWithAddressUseCase(getLocalAccounts)

    @Test
    fun `EXPECT true WHEN account is found`() = runTest {
        val localAccounts = listOf(
            ALGO_25_ACCOUNT,
            NO_AUTH_ACCOUNT,
            LEDGER_BLE_ACCOUNT
        )
        coEvery { getLocalAccounts() } returns localAccounts

        val result = sut(ALGO_25_ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN account is not found`() = runTest {
        val localAccounts = listOf(
            NO_AUTH_ACCOUNT,
            LEDGER_BLE_ACCOUNT
        )
        coEvery { getLocalAccounts() } returns localAccounts

        val result = sut(ALGO_25_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN local account list is empty`() = runTest {
        coEvery { getLocalAccounts() } returns emptyList()

        val result = sut(ALGO_25_ADDRESS)

        assertFalse(result)
    }

    companion object {
        private const val ALGO_25_ADDRESS = "ADDRESS_1"
        private const val NO_AUTH_ADDRESS = "ADDRESS_2"
        private const val LEDGER_BLE_ADDRESS = "ADDRESS_3"

        private val ALGO_25_ACCOUNT = peraFixture<Algo25>().copy(address = ALGO_25_ADDRESS)
        private val NO_AUTH_ACCOUNT = peraFixture<NoAuth>().copy(address = NO_AUTH_ADDRESS)
        private val LEDGER_BLE_ACCOUNT = peraFixture<LedgerBle>().copy(address = LEDGER_BLE_ADDRESS)
    }
}

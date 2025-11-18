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

package com.algorand.wallet.cards.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.cards.domain.repository.CardRepository
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class IsCountryWaitlistedForCardsUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mockk()
    private val cardRepository: CardRepository = mockk()

    private val sut = IsCountryWaitlistedForCardsUseCase(getLocalAccounts, cardRepository)

    @Test
    fun `EXPECT country waitlist status of auth addresses only WHEN there are no auth addresses as well`(): TestResult =
        runTest {
            coEvery { getLocalAccounts() } returns LOCAL_ACCOUNTS
            coEvery {
                cardRepository.isCountryWaitlisted(
                    listOf(
                        "ledger",
                        "hd_key",
                        "algo_25"
                    )
                )
            } returns WAITLIST_RESULT

            val result = sut()

            assertEquals(WAITLIST_RESULT, result)
        }

    private companion object {
        val LEDGER_ACCOUNT = peraFixture<LocalAccount.LedgerBle>().copy(algoAddress = "ledger")
        val HD_KEY_ACCOUNT = peraFixture<LocalAccount.HdKey>().copy(algoAddress = "hd_key")
        val NO_AUTH_ACCOUNT = peraFixture<LocalAccount.NoAuth>().copy(algoAddress = "no_auth")
        val ALGO_25_ACCOUNT = peraFixture<LocalAccount.Algo25>().copy(algoAddress = "algo_25")

        val LOCAL_ACCOUNTS = listOf(LEDGER_ACCOUNT, HD_KEY_ACCOUNT, NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)

        val WAITLIST_RESULT = PeraResult.Success(peraFixture<Boolean>())
    }
}

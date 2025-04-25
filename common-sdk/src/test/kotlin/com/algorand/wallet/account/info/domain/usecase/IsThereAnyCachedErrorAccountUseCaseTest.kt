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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.runTest

class IsThereAnyCachedErrorAccountUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = IsThereAnyCachedErrorAccountUseCase(accountInformationRepository, getLocalAccounts)

    @Test
    fun `EXPECT true WHEN there are failed accounts and excludeNoAuthAccounts is false`() = runTest {
        val failedAccounts = listOf(NO_AUTH_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(failedAccounts)

        val result = sut(excludeNoAuthAccounts = false)

        assertTrue(result)
    }

    @Test
    fun `EXPECT true WHEN there are auth failed accounts and excludeNoAuthAccounts is true`() = runTest {
        val failedAccounts = listOf(ALGO_25_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(failedAccounts)

        val result = sut(excludeNoAuthAccounts = true)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN there are no failed accounts and excludeNoAuthAccounts is false`() = runTest {
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(emptyList())

        val result = sut(excludeNoAuthAccounts = false)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN there are failed accounts but excludeNoAuthAccounts is true`() = runTest {
        val failedAccounts = listOf(NO_AUTH_ACCOUNT.algoAddress)
        val localAccounts = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(accountInformationRepository.getFailedAccountInformation()).thenReturn(failedAccounts)

        val result = sut(excludeNoAuthAccounts = true)

        assertFalse(result)
    }

    private companion object {
        val NO_AUTH_ACCOUNT = peraFixture<LocalAccount.NoAuth>()
        val ALGO_25_ACCOUNT = peraFixture<LocalAccount.Algo25>()
    }

}

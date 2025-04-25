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

package com.algorand.wallet.account.detail.domain.usecase

import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetLocalRekeyedAccountCountUseCaseTest {

    private val getLocalAccountsAddresses: GetLocalAccountsAddresses = mockk()
    private val accountInformationRepository: AccountInformationRepository = mockk()
    private val sut = GetLocalRekeyedAccountCountUseCase(getLocalAccountsAddresses, accountInformationRepository)

    @Test
    fun `EXPECT rekeyed account count WHEN invoke is called`() = runTest {
        val authAddress = "authAddress"
        val localAddresses = listOf("address1", "address2")
        val expectedCount = 2

        coEvery { getLocalAccountsAddresses() } returns localAddresses
        coEvery { accountInformationRepository.getFilteredRekeyedAccountCount(authAddress, localAddresses) } returns expectedCount

        val result = sut.invoke(authAddress)

        coVerify { getLocalAccountsAddresses() }
        coVerify { accountInformationRepository.getFilteredRekeyedAccountCount(authAddress, localAddresses) }
        assertEquals(expectedCount, result)
    }
}
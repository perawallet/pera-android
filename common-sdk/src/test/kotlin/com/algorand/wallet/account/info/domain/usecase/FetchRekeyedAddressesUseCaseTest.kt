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

package com.algorand.wallet.account.info.domain.usecase

import com.algorand.wallet.account.info.domain.model.RekeyedAddresses
import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FetchRekeyedAddressesUseCaseTest {

    private val accountInformationRepository: AccountInformationRepository = mockk(relaxed = true)
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses = mockk(relaxed = true)

    private val sut = FetchRekeyedAddressesUseCase(accountInformationRepository, getLocalAccountsAddresses)

    @Test
    fun `EXPECT error WHEN fetch fails`() = runTest {
        coEvery { accountInformationRepository.fetchRekeyedAddresses(ADDRESS) } returns PeraResult.Error(Exception())

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT rekeyed addresses WHEN fetch succeeds`() = runTest {
        val rekeyedAddresses = listOf("address1", "address2")
        coEvery {
            accountInformationRepository.fetchRekeyedAddresses(ADDRESS)
        } returns PeraResult.Success(rekeyedAddresses)
        coEvery { getLocalAccountsAddresses() } returns listOf("address1")

        val result = sut(ADDRESS)

        val expected = RekeyedAddresses(
            notImportedAddresses = listOf("address2"),
            importedAddresses = listOf("address1")
        )
        assertEquals(expected, result.getDataOrNull())
    }

    private companion object {
        const val ADDRESS = "address"
    }
}

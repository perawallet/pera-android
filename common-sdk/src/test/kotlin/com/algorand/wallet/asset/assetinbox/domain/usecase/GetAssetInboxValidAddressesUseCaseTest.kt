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

package com.algorand.wallet.asset.assetinbox.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAssetInboxValidAddressesUseCaseTest {

    private val getAccountsDetails: GetAccountsDetails = mockk()

    private val sut = GetAssetInboxValidAddressesUseCase(getAccountsDetails)

    @Test
    fun `EXPECT valid asset inbox addresses`() = runTest {
        coEvery { getAccountsDetails() } returns ACCOUNT_DETAILS

        val result = sut()

        val expected = listOf("address1", "address2")
        assertEquals(expected, result)
    }

    private companion object {
        val NO_AUTH_ACCOUNT = peraFixture<AccountDetail>().copy(
            accountType = AccountType.NoAuth
        )
        val NULL_TYPE_ACCOUNT = peraFixture<AccountDetail>().copy(
            accountType = null
        )
        val VALID_ACCOUNT_1 = peraFixture<AccountDetail>().copy(
            address = "address1",
            accountType = AccountType.Algo25
        )
        val VALID_ACCOUNT_2 = peraFixture<AccountDetail>().copy(
            address = "address2",
            accountType = AccountType.Algo25
        )

        val ACCOUNT_DETAILS = listOf(
            NO_AUTH_ACCOUNT,
            NULL_TYPE_ACCOUNT,
            VALID_ACCOUNT_1,
            VALID_ACCOUNT_2
        )
    }
}

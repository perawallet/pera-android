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

package com.algorand.wallet.account.core.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.LedgerBle
import com.algorand.wallet.account.detail.domain.model.AccountType.NoAuth
import com.algorand.wallet.account.detail.domain.model.AccountType.RekeyedAuth
import com.algorand.wallet.account.detail.domain.usecase.GetAccountDetail
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLedgerBleAccount
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.wheneverBlocking

class GetTransactionSignerUseCaseTest {

    private val getAccountDetail: GetAccountDetail = mock()
    private val getLedgerBleAccount: GetLedgerBleAccount = mock()
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress = mock {
        onBlocking { invoke(ADDRESS) } doReturn AUTH_ADDRESS
    }

    private val sut = GetTransactionSignerUseCase(getAccountDetail, getLedgerBleAccount, getAccountRekeyAdminAddress)

    @Test
    fun `EXPECT AccountNotFound WHEN signer address detail type is not found`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = null)

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.AccountNotFound(ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT Algo25 WHEN signer account type is Algo25`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = AccountType.Algo25)

        val result = sut(ADDRESS)

        val expected = TransactionSigner.Algo25(ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT LedgerBle WHEN signer account type is ledger`() = runTest {
        val ledgerBleAccount = peraFixture<LocalAccount.LedgerBle>().copy(
            algoAddress = ADDRESS
        )
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = LedgerBle)
        wheneverBlocking { getLedgerBleAccount(ADDRESS) } doReturn ledgerBleAccount

        val result = sut(ADDRESS)

        val expected = TransactionSigner.LedgerBle(
            address = ledgerBleAccount.algoAddress,
            bluetoothAddress = ledgerBleAccount.deviceMacAddress,
            positionInLedger = ledgerBleAccount.indexInLedger
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT AccountNotFound WHEN signer account type is ledger but app does not have ledger details`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = LedgerBle)
        wheneverBlocking { getLedgerBleAccount(ADDRESS) } doReturn null

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.AccountNotFound(ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT NoAuth WHEN signer account type is NoAuth`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = NoAuth)

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.NoAuth(ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT NoAuth WHEN signer is rekeyed and app does not have auth details`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = AccountType.Rekeyed)

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.NoAuth(ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT AccountNotFound WHEN signer is rekeyed auth but auth info is not cached`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = RekeyedAuth)
        wheneverBlocking { getAccountDetail("rekeyedAuth") } doReturn ACCOUNT_DETAIL.copy(
            accountRegistrationType = null
        )
        wheneverBlocking { getAccountRekeyAdminAddress(ADDRESS) } doReturn "rekeyedAuth"

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.AccountNotFound("rekeyedAuth")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT AccountNotFound WHEN signer is rekeyed auth but rekey admin address is missing`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = RekeyedAuth)
        wheneverBlocking { getAccountRekeyAdminAddress(ADDRESS) } doReturn null

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.AuthAddressNotFound(ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT Algo25 WHEN signer is rekeyed auth and auth account registration type is Algo25`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = RekeyedAuth)
        wheneverBlocking { getAccountDetail(AUTH_ADDRESS) } doReturn ACCOUNT_DETAIL.copy(
            accountRegistrationType = AccountRegistrationType.Algo25
        )

        val result = sut(ADDRESS)

        val expected = TransactionSigner.Algo25(AUTH_ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT LedgerBle WHEN signer is rekeyed auth and auth account registration type is LedgerBle`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = RekeyedAuth)
        wheneverBlocking { getAccountDetail(AUTH_ADDRESS) } doReturn ACCOUNT_DETAIL.copy(
            accountRegistrationType = AccountRegistrationType.LedgerBle
        )
        val ledgerBleAccount = peraFixture<LocalAccount.LedgerBle>().copy(algoAddress = AUTH_ADDRESS)
        wheneverBlocking { getLedgerBleAccount(AUTH_ADDRESS) } doReturn ledgerBleAccount

        val result = sut(ADDRESS)

        val expected = TransactionSigner.LedgerBle(
            address = ledgerBleAccount.algoAddress,
            bluetoothAddress = ledgerBleAccount.deviceMacAddress,
            positionInLedger = ledgerBleAccount.indexInLedger
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT NoAuth WHEN signer is rekeyed auth and auth account registration type is NoAuth`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = RekeyedAuth)
        wheneverBlocking { getAccountDetail(AUTH_ADDRESS) } doReturn ACCOUNT_DETAIL.copy(
            accountRegistrationType = AccountRegistrationType.NoAuth
        )

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.AuthAccountIsNoAuth(AUTH_ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT AuthAddressNotFound WHEN signer is rekeyed auth and auth registration type is missing`() = runTest {
        wheneverBlocking { getAccountDetail(ADDRESS) } doReturn ACCOUNT_DETAIL.copy(accountType = RekeyedAuth)
        wheneverBlocking { getAccountDetail(AUTH_ADDRESS) } doReturn ACCOUNT_DETAIL.copy(
            accountRegistrationType = null
        )

        val result = sut(ADDRESS)

        val expected = TransactionSigner.SignerNotFound.AccountNotFound(AUTH_ADDRESS)
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS = "ADDRESS"
        const val AUTH_ADDRESS = "AUTH_ADDRESS"
        val ACCOUNT_DETAIL = peraFixture<AccountDetail>().copy(
            address = ADDRESS
        )
    }
}

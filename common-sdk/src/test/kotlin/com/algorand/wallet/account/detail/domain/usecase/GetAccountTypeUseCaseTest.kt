package com.algorand.wallet.account.detail.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetAccountTypeUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mock {
        onBlocking { invoke() } doReturn LOCAL_ACCOUNTS
    }
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress = mock {
        onBlocking { invoke(ADDRESS) } doReturn null
    }

    private val sut = GetAccountTypeUseCase(getLocalAccounts, getAccountRekeyAdminAddress)

    @Test
    fun `EXPECT null WHEN account information is null`() = runTest {
        val result = sut(ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN address is not in local accounts`() = runTest {
        val localAccounts = listOf(NO_AUTH_ACCOUNT.copy(algoAddress = "no_auth_address"))
        whenever(getLocalAccounts()).thenReturn(localAccounts)

        val result = sut(ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT Algo25 WHEN account is not rekeyed and is Algo25`() = runTest {
        val localAccount = ALGO_25_ACCOUNT.copy(algoAddress = ADDRESS)
        whenever(getLocalAccounts()).thenReturn(listOf(localAccount))

        val result = sut(ADDRESS)

        assertEquals(AccountType.Algo25, result)
    }

    @Test
    fun `EXPECT LedgerBle WHEN account is not rekeyed and is LedgerBle`() = runTest {
        val localAccount = LEDGER_BLE_ACCOUNT.copy(algoAddress = ADDRESS)
        whenever(getLocalAccounts()).thenReturn(listOf(localAccount))

        val result = sut(ADDRESS)

        assertEquals(AccountType.LedgerBle, result)
    }

    @Test
    fun `EXPECT NoAuth WHEN account is not rekeyed and is NoAuth`() = runTest {
        val localAccount = NO_AUTH_ACCOUNT.copy(algoAddress = ADDRESS)
        whenever(getLocalAccounts()).thenReturn(listOf(localAccount))

        val result = sut(ADDRESS)

        assertEquals(AccountType.NoAuth, result)
    }

    @Test
    fun `EXPECT HdKey WHEN account is not rekeyed and is HdKey`() = runTest {
        val localAccount = HD_KEY_ACCOUNT.copy(algoAddress = ADDRESS)
        whenever(getLocalAccounts()).thenReturn(listOf(localAccount))

        val result = sut(ADDRESS)

        assertEquals(AccountType.HdKey, result)
    }

    @Test
    fun `EXPECT RekeyedAuth WHEN account is rekeyed and has auth signer`() = runTest {
        val localAccounts = listOf(
            NO_AUTH_ACCOUNT.copy(algoAddress = "no_auth"),
            ALGO_25_ACCOUNT.copy(algoAddress = "algo_25")
        )
        whenever(getAccountRekeyAdminAddress("no_auth")).thenReturn("algo_25")
        whenever(getLocalAccounts()).thenReturn(localAccounts)

        val result = sut("no_auth")

        assertEquals(AccountType.RekeyedAuth, result)
    }

    @Test
    fun `EXPECT Rekeyed WHEN account is rekeyed, app doesnt have auth details and its type is not NoAuth`() = runTest {
        val localAccounts = listOf(
            ALGO_25_ACCOUNT.copy(algoAddress = "rekeyed_algo_25")
        )
        whenever(getAccountRekeyAdminAddress("rekeyed_algo_25")).thenReturn("algo_25")
        whenever(getLocalAccounts()).thenReturn(localAccounts)

        val result = sut("rekeyed_algo_25")

        assertEquals(AccountType.Rekeyed, result)
    }

    @Test
    fun `EXPECT NoAuth WHEN account is rekeyed, app doesnt have auth details and its type is NoAuth`() = runTest {
        val localAccounts = listOf(
            NO_AUTH_ACCOUNT.copy(algoAddress = "no_auth")
        )
        whenever(getAccountRekeyAdminAddress("no_auth")).thenReturn("algo_25")
        whenever(getLocalAccounts()).thenReturn(localAccounts)

        val result = sut("no_auth")

        assertEquals(AccountType.NoAuth, result)
    }

    private companion object {
        const val ADDRESS = "address"

        val NO_AUTH_ACCOUNT = peraFixture<LocalAccount.NoAuth>()
        val ALGO_25_ACCOUNT = peraFixture<LocalAccount.Algo25>()
        val LEDGER_BLE_ACCOUNT = peraFixture<LocalAccount.LedgerBle>()
        val HD_KEY_ACCOUNT = peraFixture<LocalAccount.HdKey>()

        val LOCAL_ACCOUNTS = listOf(NO_AUTH_ACCOUNT, ALGO_25_ACCOUNT, LEDGER_BLE_ACCOUNT, HD_KEY_ACCOUNT)
    }
}

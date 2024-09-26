package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount.*
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountTypeUseCase
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.assertEquals
import org.mockito.kotlin.*

class GetAccountTypeUseCaseTest {

    private val getAccountInformation: GetAccountInformation = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = GetAccountTypeUseCase(getLocalAccounts, getAccountInformation)

    @Test
    fun `EXPECT Rekeyed WHEN account is rekeyed we do not have signer`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ALGO_25))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = REKEY_ADMIN_ADDRESS))

        val result = sut(ADDRESS)

        assertEquals(com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Rekeyed, result)
    }

    @Test
    fun `EXPECT RekeyedAuth WHEN account is rekeyed and we have signer`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ALGO_25, AUTH_LOCAL_ACCOUNT))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = REKEY_ADMIN_ADDRESS))

        val result = sut(ADDRESS)

        assertEquals(com.algorand.android.module.account.core.component.detail.domain.model.AccountType.RekeyedAuth, result)
    }

    @Test
    fun `EXPECT NoAuth WHEN account is rekeyed and registered as NoAuth`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_NO_AUTH))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = REKEY_ADMIN_ADDRESS))

        val result = sut(ADDRESS)

        assertEquals(com.algorand.android.module.account.core.component.detail.domain.model.AccountType.NoAuth, result)
    }

    @Test
    fun `EXPECT NoAuth when account is not rekeyed and registered as NoAuth`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_NO_AUTH))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = null))

        val result = sut(ADDRESS)

        assertEquals(com.algorand.android.module.account.core.component.detail.domain.model.AccountType.NoAuth, result)
    }

    @Test
    fun `EXPECT Algo25 WHEN account is not rekeyed and registered as Algo25`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ALGO_25))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = null))

        val result = sut(ADDRESS)

        assertEquals(com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Algo25, result)
    }

    @Test
    fun `EXPECT LedgerBle WHEN account is not rekeyed and registered as LedgerBle`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_LEDGER_BLE))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = null))

        val result = sut(ADDRESS)

        assertEquals(com.algorand.android.module.account.core.component.detail.domain.model.AccountType.LedgerBle, result)
    }

    @Test
    fun `EXPECT NotImplementedError WHEN account is not rekeyed and registered as LedgerUsb`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_LEDGER_USB))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = null))


        Assert.assertThrows(NotImplementedError::class.java) {
            runBlocking {
                sut(ADDRESS)
            }
        }
    }

    @Test
    fun `EXPECT NotImplementedError WHEN account is rekeyed and registered as LedgerUsb`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_LEDGER_USB))
        whenever(getAccountInformation(ADDRESS))
            .thenReturn(ACCOUNT_INFORMATION.copy(rekeyAdminAddress = REKEY_ADMIN_ADDRESS))

        Assert.assertThrows(NotImplementedError::class.java) {
            runBlocking {
                sut(ADDRESS)
            }
        }
    }

    companion object {
        private const val ADDRESS = "address"
        private const val REKEY_ADMIN_ADDRESS = "rekeyAdminAddress"
        private val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>().copy(address = ADDRESS)
        private val LOCAL_ALGO_25 = fixtureOf<Algo25>().copy(address = ADDRESS)
        private val LOCAL_LEDGER_BLE = fixtureOf<LedgerBle>().copy(address = ADDRESS)
        private val LOCAL_NO_AUTH = fixtureOf<NoAuth>().copy(address = ADDRESS)
        private val LOCAL_LEDGER_USB = fixtureOf<LedgerUsb>().copy(address = ADDRESS)
        private val AUTH_LOCAL_ACCOUNT = fixtureOf<Algo25>().copy(address = REKEY_ADMIN_ADDRESS)
    }
}

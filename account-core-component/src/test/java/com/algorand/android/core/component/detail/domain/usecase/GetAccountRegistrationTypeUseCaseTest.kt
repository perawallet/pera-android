package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.core.component.detail.domain.model.AccountRegistrationType.*
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountRegistrationTypeUseCase
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.kotlin.*

class GetAccountRegistrationTypeUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = GetAccountRegistrationTypeUseCase(getLocalAccounts)

    @Test
    fun `EXPECT AccountRegistrationType Algo25 WHEN type is LocalAccount Algo25`() = runTest {
        whenever(getLocalAccounts()).thenReturn(getFixtureLocalAccounts(LOCAL_ALGO_25.copy(address = ADDRESS)))

        val result = sut(ADDRESS)

        Assert.assertEquals(Algo25, result)
    }

    @Test
    fun `EXPECT AccountRegistrationType LedgerBle WHEN type is LocalAccount LedgerBle`() = runTest {
        whenever(getLocalAccounts()).thenReturn(getFixtureLocalAccounts(LOCAL_LEDGER_BLE.copy(address = ADDRESS)))

        val result = sut(ADDRESS)

        Assert.assertEquals(LedgerBle, result)
    }

    @Test
    fun `EXPECT AccountRegistrationType NoAuth WHEN type is LocalAccount NoAuth`() = runTest {
        whenever(getLocalAccounts()).thenReturn(getFixtureLocalAccounts(LOCAL_NO_AUTH.copy(address = ADDRESS)))

        val result = sut(ADDRESS)

        Assert.assertEquals(NoAuth, result)
    }

    @Test
    fun `EXPECT exception WHEN type is LocalAccount LedgerUsb`() = runTest {
        whenever(getLocalAccounts()).thenReturn(getFixtureLocalAccounts(fixtureOf<LocalAccount.LedgerUsb>().copy(address = ADDRESS)))

        Assert.assertThrows(NotImplementedError::class.java) {
            runBlocking {
                sut(ADDRESS)
            }
        }
    }

    companion object {
        private const val ADDRESS = "address"

        private val LOCAL_ALGO_25 = fixtureOf<LocalAccount.Algo25>()
        private val LOCAL_LEDGER_BLE = fixtureOf<LocalAccount.LedgerBle>()
        private val LOCAL_NO_AUTH = fixtureOf<LocalAccount.NoAuth>()

        private fun getFixtureLocalAccounts(localAccount: LocalAccount): List<LocalAccount> {
            return listOf(LOCAL_ALGO_25, LOCAL_LEDGER_BLE, LOCAL_NO_AUTH, localAccount)
        }
    }
}

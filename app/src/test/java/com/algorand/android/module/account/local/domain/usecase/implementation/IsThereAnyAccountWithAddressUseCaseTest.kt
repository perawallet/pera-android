package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.model.LocalAccount.*
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

class IsThereAnyAccountWithAddressUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = IsThereAnyAccountWithAddressUseCase(getLocalAccounts)

    @Test
    fun `EXPECT true WHEN account is found`() = runTest {
        val localAccounts = listOf(
            ALGO_25_ACCOUNT,
            NO_AUTH_ACCOUNT,
            LEDGER_BLE_ACCOUNT
        )
        whenever(getLocalAccounts()).thenReturn(localAccounts)

        val result = sut(ALGO_25_ADDRESS)

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN account is not found`() = runTest {
        val localAccounts = listOf(
            NO_AUTH_ACCOUNT,
            LEDGER_BLE_ACCOUNT
        )
        whenever(getLocalAccounts()).thenReturn(localAccounts)

        val result = sut(ALGO_25_ADDRESS)

        assertFalse(result)
    }

    @Test
    fun `EXPECT false WHEN local account list is empty`() = runTest {
        whenever(getLocalAccounts()).thenReturn(emptyList())

        val result = sut(ALGO_25_ADDRESS)

        assertFalse(result)
    }

    companion object {
        private const val ALGO_25_ADDRESS = "ADDRESS_1"
        private const val NO_AUTH_ADDRESS = "ADDRESS_2"
        private const val LEDGER_BLE_ADDRESS = "ADDRESS_3"

        private val ALGO_25_ACCOUNT = fixtureOf<Algo25>().copy(address = ALGO_25_ADDRESS)
        private val NO_AUTH_ACCOUNT = fixtureOf<NoAuth>().copy(address = NO_AUTH_ADDRESS)
        private val LEDGER_BLE_ACCOUNT = fixtureOf<LedgerBle>().copy(address = LEDGER_BLE_ADDRESS)
    }
}

package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.repository.*
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class GetLocalAccountsUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mock()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mock()
    private val ledgerUsbAccountRepository: LedgerUsbAccountRepository = mock()
    private val noAuthAccountRepository: NoAuthAccountRepository = mock()

    private val sut = GetLocalAccountsUseCase(
        algo25AccountRepository,
        ledgerBleAccountRepository,
        ledgerUsbAccountRepository,
        noAuthAccountRepository
    )

    @Test(timeout = 200L)
    fun `EXPECT local accounts and all calls to be made async`() = runTest {
        whenever(algo25AccountRepository.getAll()).doAnswer { Thread.sleep(100L); ALGO_25_ACCOUNTS }
        whenever(ledgerBleAccountRepository.getAll()).doAnswer { Thread.sleep(100L); LEDGER_BLE_ACCOUNTS }
        whenever(ledgerUsbAccountRepository.getAll()).doAnswer { Thread.sleep(100L); LEDGER_USB_ACCOUNTS }
        whenever(noAuthAccountRepository.getAll()).doAnswer { Thread.sleep(100L); NO_AUTH_ACCOUNTS }

        val result = sut()

        val expected = ALGO_25_ACCOUNTS + LEDGER_BLE_ACCOUNTS + LEDGER_USB_ACCOUNTS + NO_AUTH_ACCOUNTS
        assertEquals(expected, result)
    }

    companion object {
        private val ALGO_25_ACCOUNTS = fixtureOf<List<LocalAccount.Algo25>>()
        private val LEDGER_BLE_ACCOUNTS = fixtureOf<List<LocalAccount.LedgerBle>>()
        private val LEDGER_USB_ACCOUNTS = fixtureOf<List<LocalAccount.LedgerUsb>>()
        private val NO_AUTH_ACCOUNTS = fixtureOf<List<LocalAccount.NoAuth>>()
    }
}

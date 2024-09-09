package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.repository.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

internal class DeleteLocalAccountUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mock()
    private val noAuthAccountRepository: NoAuthAccountRepository = mock()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mock()

    private val deleteLocalAccount = DeleteLocalAccountUseCase(
        algo25AccountRepository = algo25AccountRepository,
        noAuthAccountRepository = noAuthAccountRepository,
        ledgerBleAccountRepository = ledgerBleAccountRepository
    )

    @Test
    fun `EXPECT all account repositories to delete account`() = runTest {
        deleteLocalAccount("address")

        verify(algo25AccountRepository).deleteAccount("address")
        verify(noAuthAccountRepository).deleteAccount("address")
        verify(ledgerBleAccountRepository).deleteAccount("address")
    }
}

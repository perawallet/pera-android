package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.usecase.CreateLedgerBleAccount
import com.algorand.android.module.account.local.domain.usecase.DeleteLocalAccount
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class UpdateNoAuthAccountToLedgerBleUseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mock()
    private val createLedgerBleAccount: CreateLedgerBleAccount = mock()

    private val sut = UpdateNoAuthAccountToLedgerBleUseCase(deleteLocalAccount, createLedgerBleAccount)

    @Test
    fun `EXPECT noAuthAccount to be deleted and new LedgerBleAccount to be created`() = runTest {
        sut(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER)

        verify(deleteLocalAccount).invoke(ADDRESS)
        verify(createLedgerBleAccount).invoke(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER)
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
        private const val DEVICE_MAC_ADDRESS = "DEVICE_MAC_ADDRESS"
        private const val INDEX_IN_LEDGER = 0
    }
}

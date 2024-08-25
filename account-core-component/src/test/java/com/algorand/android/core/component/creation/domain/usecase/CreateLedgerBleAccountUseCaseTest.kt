package com.algorand.android.core.component.creation.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.AddLedgerBleAccount
import com.algorand.android.core.component.caching.domain.usecase.CacheAccountDetail
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class CreateLedgerBleAccountUseCaseTest {

    private val addLedgerBleAccount: AddLedgerBleAccount = mock()
    private val cacheAccountDetail: CacheAccountDetail = mock()

    private val sut = CreateLedgerBleAccountUseCase(addLedgerBleAccount, cacheAccountDetail)

    @Test
    fun `EXPECT ledger ble account to be created and cached successfully`() = runTest {
        sut("address", "deviceMacAddress", 0)

        verify(addLedgerBleAccount).invoke(LocalAccount.LedgerBle("address", "deviceMacAddress", 0))
        verify(cacheAccountDetail).invoke("address")
    }
}

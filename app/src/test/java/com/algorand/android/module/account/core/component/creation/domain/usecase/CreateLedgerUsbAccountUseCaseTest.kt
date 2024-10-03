package com.algorand.android.module.account.core.component.creation.domain.usecase

import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.usecase.AddLedgerUsbAccount
import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class CreateLedgerUsbAccountUseCaseTest {

    private val addLedgerUsbAccount: AddLedgerUsbAccount = mock()
    private val cacheAccountDetail: CacheAccountDetail = mock()

    private val sut = CreateLedgerUsbAccountUseCase(addLedgerUsbAccount, cacheAccountDetail)

    @Test
    fun `EXPECT ledger usb account to be created and cached successfully`() = runTest {
        sut("address", 0, 0)

        verify(addLedgerUsbAccount).invoke(LocalAccount.LedgerUsb("address", 0, 0))
        verify(cacheAccountDetail).invoke("address")
    }
}

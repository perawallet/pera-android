package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.local.domain.usecase.CreateAlgo25Account
import com.algorand.android.module.account.local.domain.usecase.CreateLedgerBleAccount
import com.algorand.android.module.account.local.domain.usecase.CreateNoAuthAccount
import com.algorand.android.module.account.sorting.domain.usecase.SetAccountOrderIndex
import com.algorand.android.module.asb.domain.usecase.SetAccountAsbBackUpStatus
import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.custominfo.domain.usecase.SetCustomInfo
import kotlin.Int.Companion.MAX_VALUE
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class AddAccountUseCaseTest {

    private val createAlgo25Account: CreateAlgo25Account = mock()
    private val createLedgerBleAccount: CreateLedgerBleAccount = mock()
    private val createNoAuthAccount: CreateNoAuthAccount = mock()
    private val setCustomInfo: SetCustomInfo = mock()
    private val setAccountAsbBackUpStatus: SetAccountAsbBackUpStatus = mock()
    private val setAccountOrderIndex: SetAccountOrderIndex = mock()

    private val sut = AddAccountUseCase(
        createAlgo25Account,
        createLedgerBleAccount,
        createNoAuthAccount,
        setCustomInfo,
        setAccountAsbBackUpStatus,
        setAccountOrderIndex
    )

    @Test
    fun `EXPECT algo25 account to be added and custom info to be set WHEN addAlgo25 is called`() = runTest {
        sut.addAlgo25(ADDRESS, SECRET_KEY, IS_BACKED_UP, CUSTOM_NAME)

        verify(createAlgo25Account).invoke(ADDRESS, SECRET_KEY)
        verify(setCustomInfo).invoke(CustomInfo(ADDRESS, CUSTOM_NAME))
        verify(setAccountOrderIndex).invoke(ADDRESS, MAX_VALUE)
        verify(setAccountAsbBackUpStatus).invoke(ADDRESS, IS_BACKED_UP)
    }

    @Test
    fun `EXPECT ledger ble account to be added and custom info to be set WHEN addLedgerBle is called`() = runTest {
        sut.addLedgerBle(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER, CUSTOM_NAME)

        verify(createLedgerBleAccount).invoke(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER)
        verify(setCustomInfo).invoke(CustomInfo(ADDRESS, CUSTOM_NAME))
        verify(setAccountOrderIndex).invoke(ADDRESS, MAX_VALUE)
        verify(setAccountAsbBackUpStatus).invoke(ADDRESS, true)
    }

    @Test
    fun `EXPECT no auth account to be added and custom info to be set WHEN addNoAuth is called`() = runTest {
        sut.addNoAuth(ADDRESS, CUSTOM_NAME)

        verify(createNoAuthAccount).invoke(ADDRESS)
        verify(setCustomInfo).invoke(CustomInfo(ADDRESS, CUSTOM_NAME))
        verify(setAccountOrderIndex).invoke(ADDRESS, MAX_VALUE)
        verify(setAccountAsbBackUpStatus).invoke(ADDRESS, true)
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
        private val SECRET_KEY = ByteArray(0)
        private const val IS_BACKED_UP = true
        private const val CUSTOM_NAME = "CUSTOM_NAME"
        private const val DEVICE_MAC_ADDRESS = "DEVICE_MAC_ADDRESS"
        private const val INDEX_IN_LEDGER = 0
    }
}

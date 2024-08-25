package com.algorand.android.core.component.domain.usecase

import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.custominfo.component.domain.usecase.SetCustomInfo
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.Int.Companion.MAX_VALUE

internal class AddAccountUseCaseTest {

    private val createAlgo25Account: CreateAlgo25Account = mock()
    private val createLedgerBleAccount: CreateLedgerBleAccount = mock()
    private val createNoAuthAccount: CreateNoAuthAccount = mock()
    private val setCustomInfo: SetCustomInfo = mock()

    private val sut = AddAccountUseCase(
        createAlgo25Account,
        createLedgerBleAccount,
        createNoAuthAccount,
        setCustomInfo
    )

    @Test
    fun `EXPECT algo25 account to be added and custom info to be set WHEN addAlgo25 is called`() = runTest {
        sut.addAlgo25(ADDRESS, SECRET_KEY, IS_BACKED_UP, CUSTOM_NAME)

        verify(createAlgo25Account).invoke(ADDRESS, SECRET_KEY)
        verify(setCustomInfo).invoke(
            CustomInfo(
                ADDRESS,
                MAX_VALUE,
                IS_BACKED_UP,
                CUSTOM_NAME
            )
        )
    }

    @Test
    fun `EXPECT ledger ble account to be added and custom info to be set WHEN addLedgerBle is called`() = runTest {
        sut.addLedgerBle(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER, CUSTOM_NAME)

        verify(createLedgerBleAccount).invoke(ADDRESS, DEVICE_MAC_ADDRESS, INDEX_IN_LEDGER)
        verify(setCustomInfo).invoke(
            CustomInfo(
                ADDRESS,
                MAX_VALUE,
                true,
                CUSTOM_NAME
            )
        )
    }

    @Test
    fun `EXPECT no auth account to be added and custom info to be set WHEN addNoAuth is called`() = runTest {
        sut.addNoAuth(ADDRESS, CUSTOM_NAME)

        verify(createNoAuthAccount).invoke(ADDRESS)
        verify(setCustomInfo).invoke(
            CustomInfo(
                ADDRESS,
                MAX_VALUE,
                true,
                CUSTOM_NAME
            )
        )
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

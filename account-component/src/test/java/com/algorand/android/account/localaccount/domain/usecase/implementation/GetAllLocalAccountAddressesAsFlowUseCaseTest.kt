package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.model.LocalAccount.*
import com.algorand.android.module.account.local.domain.repository.*
import com.algorand.android.testutil.*
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.mockito.kotlin.*

class GetAllLocalAccountAddressesAsFlowUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mock()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mock()
    private val ledgerUsbAccountRepository: LedgerUsbAccountRepository = mock()
    private val noAuthAccountRepository: NoAuthAccountRepository = mock()

    private val sut = GetAllLocalAccountAddressesAsFlowUseCase(
        algo25AccountRepository,
        ledgerBleAccountRepository,
        ledgerUsbAccountRepository,
        noAuthAccountRepository
    )

    @Test
    fun `EXPECT empty list when all repositories return empty list`() {
        whenever(algo25AccountRepository.getAllAsFlow()).thenReturn(flowOf(emptyList()))
        whenever(ledgerBleAccountRepository.getAllAsFlow()).thenReturn(flowOf(emptyList()))
        whenever(ledgerUsbAccountRepository.getAllAsFlow()).thenReturn(flowOf(emptyList()))
        whenever(noAuthAccountRepository.getAllAsFlow()).thenReturn(flowOf(emptyList()))

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(emptyList())
    }

    @Test
    fun `EXPECT account addresses WHEN there are local accounts`() {
        whenever(algo25AccountRepository.getAllAsFlow()).thenReturn(flowOf(listOf(ALGO_25_ACCOUNT)))
        whenever(ledgerBleAccountRepository.getAllAsFlow()).thenReturn(flowOf(listOf(LEDGER_BLE_ACCOUNT)))
        whenever(ledgerUsbAccountRepository.getAllAsFlow()).thenReturn(flowOf(listOf(LEDGER_USB_ACCOUNT)))
        whenever(noAuthAccountRepository.getAllAsFlow()).thenReturn(flowOf(listOf(NO_AUTH_ACCOUNT)))

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(listOf(ALGO_25_ADDRESS, LEDGER_BLE_ADDRESS, LEDGER_USB_ADDRESS, NO_AUTH_ADDRESS))
    }

    companion object {
        private const val ALGO_25_ADDRESS = "address1"
        private val ALGO_25_ACCOUNT = fixtureOf<Algo25>().copy(address = ALGO_25_ADDRESS)
        private const val LEDGER_BLE_ADDRESS = "address2"
        private val LEDGER_BLE_ACCOUNT = fixtureOf<LedgerBle>().copy(address = LEDGER_BLE_ADDRESS)
        private const val LEDGER_USB_ADDRESS = "address3"
        private val LEDGER_USB_ACCOUNT = fixtureOf<LedgerUsb>().copy(address = LEDGER_USB_ADDRESS)
        private const val NO_AUTH_ADDRESS = "address4"
        private val NO_AUTH_ACCOUNT = fixtureOf<NoAuth>().copy(address = NO_AUTH_ADDRESS)
    }
}

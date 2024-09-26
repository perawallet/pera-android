package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.repository.Algo25AccountRepository
import com.algorand.android.module.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.android.module.account.local.domain.repository.LedgerUsbAccountRepository
import com.algorand.android.module.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.android.testutil.test
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetLocalAccountCountFlowUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mock()
    private val ledgerBleAccountRepository: LedgerBleAccountRepository = mock()
    private val ledgerUsbAccountRepository: LedgerUsbAccountRepository = mock()
    private val noAuthAccountRepository: NoAuthAccountRepository = mock()

    private val sut = GetLocalAccountCountFlowUseCase(
        algo25AccountRepository,
        ledgerBleAccountRepository,
        ledgerUsbAccountRepository,
        noAuthAccountRepository
    )

    @Test
    fun `EXPECT zero WHEN all repositories return zero`() {
        whenever(algo25AccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(0))
        whenever(ledgerBleAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(0))
        whenever(ledgerUsbAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(0))
        whenever(noAuthAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(0))

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(0)
    }

    @Test
    fun `EXPECT account count WHEN there are local accounts`() {
        whenever(algo25AccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(1))
        whenever(ledgerBleAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(2))
        whenever(ledgerUsbAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(3))
        whenever(noAuthAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(4))

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValue(10)
    }

    @Test
    fun `EXPECT count to be updated WHEN latest count is different than the current one`() {
        whenever(algo25AccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(1, 1))
        whenever(ledgerBleAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(2, 2))
        whenever(ledgerUsbAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(3, 3))
        whenever(noAuthAccountRepository.getAccountCountAsFlow()).thenReturn(flowOf(4, 4))

        val testObserver = sut().test()

        testObserver.stopObserving()
        testObserver.assertValueHistory(10)
    }
}

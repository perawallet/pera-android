package com.algorand.android.module.account.core.component.polling.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.accountinfo.component.domain.usecase.GetEarliestLastFetchedRound
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.repository.AccountBlockPollingRepository
import com.algorand.android.module.account.core.component.polling.accountdetail.domain.usecase.implementation.IsAccountCacheUpdateRequiredUseCase
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

class IsAccountCacheUpdateRequiredUseCaseTest {

    private val getEarliestLastFetchedRound: GetEarliestLastFetchedRound = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()
    private val blockPollingRepository: AccountBlockPollingRepository = mock()

    private val sut = IsAccountCacheUpdateRequiredUseCase(
        getEarliestLastFetchedRound,
        getLocalAccounts,
        blockPollingRepository
    )

    @Test
    fun `EXPECT repository result WHEN invoked`() = runTest {
        whenever(getLocalAccounts()).thenReturn(LOCAL_ACCOUNTS)
        whenever(getEarliestLastFetchedRound()).thenReturn(0L)
        whenever(blockPollingRepository.isAccountUpdateRequired(LOCAL_ACCOUNTS.map { it.address }, 0L))
            .thenReturn(Result.success(true))

        val result = sut()

        val expected = Result.success(true)
        assertEquals(expected.isSuccess, result.isSuccess)
        assertEquals(expected.isSuccess, result.getOrNull())
    }

    companion object {
        private val LOCAL_ACCOUNT_1 = fixtureOf<LocalAccount.Algo25>().copy(address = "address1")
        private val LOCAL_ACCOUNT_2 = fixtureOf<LocalAccount.Algo25>().copy(address = "address2")
        private val LOCAL_ACCOUNTS = listOf(LOCAL_ACCOUNT_1, LOCAL_ACCOUNT_2)
    }
}
